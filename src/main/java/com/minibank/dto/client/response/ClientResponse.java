package com.minibank.dto.client.response;

import com.minibank.dto.account.response.AccountResponse;

import java.util.List;

public record ClientResponse(
        Long id,
        String name,
        String email,
        List<AccountResponse> accounts
) {
}
