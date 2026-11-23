package com.minibank.dto.error;

public record HttpErrorResponse(
        int statusCode,
        String message
) {
}
