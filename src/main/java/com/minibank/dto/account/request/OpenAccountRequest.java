package com.minibank.dto.account.request;

public record OpenAccountRequest(
        Long clientId,
        String accountNumber
) {
}
