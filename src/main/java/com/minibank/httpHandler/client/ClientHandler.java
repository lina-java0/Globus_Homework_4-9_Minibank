package com.minibank.httpHandler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.BankService;
import com.minibank.Client;
import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.minibank.validator.client.ClientValidator;
import com.minibank.validator.client.RequestBodyNullClientValidator;
import com.minibank.validator.client.RequestNullClientValidator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements HttpHandler {

    private final BankService bankService;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;
    private final List<ClientValidator> clientValidators;

    public ClientHandler(
            BankService bankService,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.bankService = bankService;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;

        this.clientValidators = List.of(
                new RequestNullClientValidator(),
                new RequestBodyNullClientValidator()
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            validateHttpMethod(exchange);

            // Читаем JSON из тела запроса
            RegisterClientRequest request = readRequest(exchange);

            // Валидация
            for (ClientValidator validator : clientValidators) {
                validator.validateRequest(request);
            }

            //Вызываем сервис
            Client client = bankService.registerClient(
                    request.name(),
                    request.email()
            );

            sendResponse(
                    exchange,
                    201,
                    objectMapper.writeValueAsString(client));

        } catch (RuntimeException ex) {
            HttpErrorResponse error = exceptionMapper.map(ex);

            sendResponse(
                    exchange,
                    error.statusCode(),
                    objectMapper.writeValueAsString(error)
            );
        }
    }

    private void validateHttpMethod(HttpExchange exchange) {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            throw new MethodNotAllowedException("Method Not Allowed");
        }
    }

    private RegisterClientRequest readRequest(HttpExchange exchange) {
        try (InputStream requestBody = exchange.getRequestBody()) {
            return objectMapper.readValue(
                    requestBody,
                    RegisterClientRequest.class
            );
        } catch (IOException ex) {
            throw new InvalidRequestException("Invalid request");
        }
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
