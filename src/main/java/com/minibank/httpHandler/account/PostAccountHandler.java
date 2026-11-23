package com.minibank.httpHandler.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.dto.account.OpenAccountRequest;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.httpHandler.utils.HttpUtils;
import com.minibank.model.Account;
import com.minibank.service.BankService;
import com.minibank.validator.account.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class PostAccountHandler implements HttpHandler {

    private final BankService bankService;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;
    private final List<AccountValidator> accountValidators;

    public PostAccountHandler(
            BankService bankService,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.bankService = bankService;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;

        this.accountValidators = List.of(
                new RequestNullAccountValidator(),
                new RequestBodyNullAccountValidator(),
                new PositiveClientIdAccountValidator(),
                new AccountNumberBlankAccountValidator()
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Формирование запроса
            OpenAccountRequest request = HttpUtils.readRequest(
                    exchange,
                    objectMapper,
                    OpenAccountRequest.class
            );
            // Валидация запроса
            for (AccountValidator validator : accountValidators) {
                validator.validateRequest(request);
            }
            // Вызов сервиса
            Account account = bankService.openAccount(
                    request.clientId(),
                    request.accountNumber()
            );
            HttpUtils.sendResponse(
                    exchange,
                    201,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(account)
            );

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
