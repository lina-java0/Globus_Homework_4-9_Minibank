package com.minibank.dto.client.request;

public record RegisterClientRequest(
        String name,
        String email
) {
}
