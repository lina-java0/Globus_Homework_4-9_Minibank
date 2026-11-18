package com.minibank.httpHandler.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.BankService;
import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.minibank.validator.transfer.AmountTransferValidator;
import com.minibank.validator.transfer.RequestBodyNullTransferValidator;
import com.minibank.validator.transfer.RequestNullTransferValidator;
import com.minibank.validator.transfer.TransferValidator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TransferHandler implements HttpHandler {

    private final BankService bankService;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;
    private final List<TransferValidator> transferValidators;

    public TransferHandler(
            BankService bankService,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.bankService = bankService;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;

        this.transferValidators = List.of(
                new RequestNullTransferValidator(),
                new RequestBodyNullTransferValidator(),
                new AmountTransferValidator()
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            validateHttpMethod(exchange);

            // Читаем JSON из тела запроса
            TransferRequest request = readRequest(exchange);

            // Валидация
            for (TransferValidator validator : transferValidators) {
                validator.validateRequest(request);
            }

            //Вызываем сервис
            bankService.transfer(
                    request.fromAccountId(),
                    request.toAccountId(),
                    request.amount()
            );

            TransferResponse response = new TransferResponse(
                    "Transfer successful"
            );

            sendResponse(
                    exchange,
                    200,
                    objectMapper.writeValueAsString(response)
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

    private TransferRequest readRequest(HttpExchange exchange) {
        try (InputStream requestBody = exchange.getRequestBody()) {
            return objectMapper.readValue(
                    requestBody,
                    TransferRequest.class
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
