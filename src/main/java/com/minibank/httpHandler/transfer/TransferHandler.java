package com.minibank.httpHandler.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.dto.transfer.TransferRequest;
import com.minibank.dto.transfer.TransferResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.minibank.httpHandler.utils.HttpUtils;
import com.minibank.service.BankService;
import com.minibank.validator.transfer.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
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
                new PositiveAccountIdTransferValidator(),
                new AmountTransferValidator()
        );
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            validateHttpMethod(exchange);

            // Формирование запроса
            TransferRequest request = HttpUtils.readRequest(
                    exchange,
                    objectMapper,
                    TransferRequest.class
            );
            // Валидация запроса
            for (TransferValidator validator : transferValidators) {
                validator.validateRequest(request);
            }
            // Вызов сервиса
            bankService.transfer(
                    request.fromAccountId(),
                    request.toAccountId(),
                    request.amount()
            );
            TransferResponse response = new TransferResponse("Transfer successful");
            HttpUtils.sendResponse(
                    exchange,
                    200,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)
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

    private void validateHttpMethod(HttpExchange exchange) {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            throw new MethodNotAllowedException("Method not allowed");
        }
    }
}
