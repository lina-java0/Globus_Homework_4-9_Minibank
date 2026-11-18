package com.minibank.httpHandler.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtils {

    // Валидация HTTP метода
    public static void validateHttpMethod(HttpExchange exchange) {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            throw new MethodNotAllowedException("Method not allowed");
        }
    }

    // Чтение JSON из тела запроса
    public static <T> T readRequest(
            HttpExchange exchange,
            ObjectMapper objectMapper,
            Class<T> requestClass
    ) {
        try (InputStream requestBody = exchange.getRequestBody()) {
            byte[] body = requestBody.readAllBytes();
            if (body.length == 0) {
                return null;
            }
            return objectMapper.readValue(body, requestClass);
        } catch (IOException ex) {
            throw new InvalidRequestException("Invalid JSON");
        }
    }

    // Формирование JSON ответа
    public static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String message
    ) throws IOException {
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
