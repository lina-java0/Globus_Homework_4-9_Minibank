package com.minibank.httpHandler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.httpHandler.utils.HttpUtils;
import com.minibank.model.Client;
import com.minibank.service.BankService;
import com.minibank.validator.client.ClientValidator;
import com.minibank.validator.client.RequestBodyBlankClientValidator;
import com.minibank.validator.client.RequestBodyNullClientValidator;
import com.minibank.validator.client.RequestNullClientValidator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
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
                new RequestBodyNullClientValidator(),
                new RequestBodyBlankClientValidator()
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpUtils.validateHttpMethod(exchange);
            // Формирование запроса
            RegisterClientRequest request = HttpUtils.readRequest(
                    exchange,
                    objectMapper,
                    RegisterClientRequest.class
            );
            // Валидация запроса
            for (ClientValidator validator : clientValidators) {
                validator.validateRequest(request);
            }
            // Вызов сервиса
            Client client = bankService.registerClient(
                    request.name(),
                    request.email()
            );
            HttpUtils.sendResponse(
                    exchange,
                    201,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(client));

        } catch (RuntimeException ex) {
            HttpErrorResponse error = exceptionMapper.map(ex);

            HttpUtils.sendResponse(
                    exchange,
                    error.statusCode(),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(error)
            );
        }
    }
}
