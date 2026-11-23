package com.minibank.httpHandler.client.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.minibank.exception.custom_exceptions.RouteNotFoundException;
import com.minibank.httpHandler.client.GetClientHandler;
import com.minibank.httpHandler.client.PostClientHandler;
import com.minibank.httpHandler.utils.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ClientRouter implements HttpHandler {

    private final GetClientHandler getClientHandler;
    private final PostClientHandler postClientHandler;
    private final ObjectMapper objectMapper;
    private final HttpExceptionMapper exceptionMapper;

    public ClientRouter(
            GetClientHandler getClientHandler,
            PostClientHandler postClientHandler,
            ObjectMapper objectMapper,
            HttpExceptionMapper exceptionMapper
    ) {
        this.getClientHandler = getClientHandler;
        this.postClientHandler = postClientHandler;
        this.objectMapper = objectMapper;
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Сохраняем завершающий слэш, чтобы различать /clients и /clients/
        String[] parts = path.split("/", -1);

        if ("/clients".equals(path)) {

            if ("POST".equalsIgnoreCase(method)) {
                postClientHandler.handle(exchange);
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
                && "clients".equals(parts[1])
                && !parts[2].isEmpty()) {

            if ("GET".equalsIgnoreCase(method)) {
                getClientHandler.handle(exchange);
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
