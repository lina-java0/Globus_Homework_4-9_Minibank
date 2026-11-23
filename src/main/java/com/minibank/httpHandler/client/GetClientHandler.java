package com.minibank.httpHandler.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.dto.client.response.ClientResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.httpHandler.utils.HttpUtils;
import com.minibank.service.BankService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class GetClientHandler implements HttpHandler {

    private final BankService bankService;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;

    public GetClientHandler(
            BankService bankService,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.bankService = bankService;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Формирование запроса
            long clientId = HttpUtils.extractId(exchange, "clients");

            // Вызов сервиса
            ClientResponse client = bankService.getClient(clientId);

            HttpUtils.sendResponse(
                    exchange,
                    200,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(client));

        } catch (RuntimeException ex) {
            HttpUtils.sendError(
                    exchange,
                    ex,
                    exceptionMapper,
                    objectMapper
            );
        }
    }
}
