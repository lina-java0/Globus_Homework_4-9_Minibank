package com.minibank.httpHandler.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.dto.account.response.AccountResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.httpHandler.utils.HttpUtils;
import com.minibank.service.BankService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class GetAccountHandler implements HttpHandler {

    private final BankService bankService;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;

    public GetAccountHandler(
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
            long accountId = HttpUtils.extractId(exchange, "accounts");

            // Вызов сервиса
            AccountResponse account = bankService.getAccount(accountId);

            HttpUtils.sendResponse(
                    exchange,
                    200,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(account));

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
