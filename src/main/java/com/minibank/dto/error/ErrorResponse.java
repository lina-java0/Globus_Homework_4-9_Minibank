package com.minibank.dto.error;

public record ErrorResponse(
        int statusCode,
        String message
) {
}
