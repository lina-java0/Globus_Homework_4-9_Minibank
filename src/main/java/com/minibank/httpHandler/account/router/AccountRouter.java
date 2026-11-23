package com.minibank.httpHandler.account.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.minibank.exception.custom_exceptions.RouteNotFoundException;
import com.minibank.httpHandler.account.GetAccountHandler;
import com.minibank.httpHandler.account.PostAccountHandler;
import com.minibank.httpHandler.utils.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class AccountRouter implements HttpHandler {

    private final GetAccountHandler getAccountHandler;
    private final PostAccountHandler postAccountHandler;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;

    public AccountRouter(
            GetAccountHandler getAccountHandler,
            PostAccountHandler postAccountHandler,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.getAccountHandler = getAccountHandler;
        this.postAccountHandler = postAccountHandler;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Сохраняем завершающий слэш, чтобы различать /clients и /clients/
        String[] parts = path.split("/", -1);

        if ("/accounts".equals(path)) {

            if ("POST".equalsIgnoreCase(method)) {
                postAccountHandler.handle(exchange);
                return;
            }

            exchange.getResponseHeaders().set("Allow", "POST");
            HttpUtils.sendError(
                    exchange,
                    new MethodNotAllowedException("Method not allowed"),
                    exceptionMapper,
                    objectMapper
            );
            return;
        }

        if (parts.length == 3
                && "accounts".equals(parts[1])
                && !parts[2].isEmpty()) {

            if ("GET".equalsIgnoreCase(method)) {
                getAccountHandler.handle(exchange);
                return;
            }

            exchange.getResponseHeaders().set("Allow", "GET");
            HttpUtils.sendError(
                    exchange,
                    new MethodNotAllowedException("Method not allowed"),
                    exceptionMapper,
                    objectMapper
            );
            return;
        }

        HttpUtils.sendError(
                exchange,
                new RouteNotFoundException("Route not found"),
                exceptionMapper,
                objectMapper
        );
    }
}