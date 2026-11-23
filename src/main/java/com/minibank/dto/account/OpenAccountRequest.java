package com.minibank.dto.account;

public record OpenAccountRequest(
        Long clientId,
        String accountNumber
) {
}
