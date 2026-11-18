package com.minibank.httpHandler.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.Account;
import com.minibank.BankService;
import com.minibank.dto.account.OpenAccountRequest;
import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.minibank.validator.account.AccountValidator;
import com.minibank.validator.account.RequestBodyNullAccountValidator;
import com.minibank.validator.account.RequestNullAccountValidator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AccountHandler implements HttpHandler {

    private final BankService bankService;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;
    private final List<AccountValidator> accountValidators;

    public AccountHandler(
            BankService bankService,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.bankService = bankService;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;

        this.accountValidators = List.of(
                new RequestNullAccountValidator(),
                new RequestBodyNullAccountValidator()
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            validateHttpMethod(exchange);

            // Читаем JSON из тела запроса
            OpenAccountRequest request = readRequest(exchange);

            // Валидация
            for (AccountValidator validator : accountValidators) {
                validator.validateRequest(request);
            }

            // Вызываем сервис
            Account account = bankService.openAccount(
                    request.clientId(),
                    request.accountNumber()
            );

            sendResponse(
                    exchange,
                    201,
                    objectMapper.writeValueAsString(account)
            );

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
            throw new MethodNotAllowedException("Method not allowed");
        }
    }

    private OpenAccountRequest readRequest(HttpExchange exchange) {
        try (InputStream requestBody = exchange.getRequestBody()) {
            return objectMapper.readValue(
                    requestBody,
                    OpenAccountRequest.class
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
