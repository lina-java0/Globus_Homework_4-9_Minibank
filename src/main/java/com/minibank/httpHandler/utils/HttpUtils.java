package com.minibank.httpHandler.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.HttpExceptionMapper;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtils {

    // Извлечение ID из URI
    public static long extractId(HttpExchange exchange, String resource) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 3 || !parts[1].equals(resource)) {
            throw new InvalidRequestException("Path must contain /" + resource + "/{id}");
        }
        try {
            long id = Long.parseLong(parts[2]);

            if (id <= 0) {
                throw new InvalidRequestException("Id must be positive");
            }
            return id;
        } catch (NumberFormatException ex) {
            throw new InvalidRequestException("Id must be a number");
        }
    }

    // Чтение JSON из тела запроса
    public static <T> T readRequest(
            HttpExchange exchange,
            ObjectMapper objectMapper,
            Class<T> requestClass
    ) throws IOException {
        byte[] body;

        try (InputStream requestBody = exchange.getRequestBody()) {
            body = requestBody.readAllBytes();
        }

        if (body.length == 0) {return null;}

        try {
            return objectMapper.readValue(body, requestClass);
        } catch (JsonProcessingException ex) {
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

    // Отправка ошибок
    public static void sendError(
            HttpExchange exchange,
            Exception exception,
            HttpExceptionMapper exceptionMapper,
            ObjectMapper objectMapper
    ) throws IOException {

        HttpErrorResponse error = exceptionMapper.map(exception);

        sendResponse(
                exchange,
                error.statusCode(),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(error)
        );
    }
}
