package com.minibank.dto.client;

public record RegisterClientRequest(
        String name,
        String email
) {
}
