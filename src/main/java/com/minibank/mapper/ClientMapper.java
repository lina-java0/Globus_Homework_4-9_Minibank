package com.minibank.mapper;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.dto.client.response.ClientResponse;
import com.minibank.model.Client;

import java.util.List;

public class ClientMapper {

    private final AccountMapper accountMapper;

    public ClientMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public ClientResponse toResponse(Client client, List<AccountResponse> accounts) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                accounts
        );
    }

    public ClientResponse toResponse(Client client) {

        List<AccountResponse> accounts = client.getAccounts()
                .stream()
                .map(accountMapper::toResponse)
                .toList();

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                accounts
        );
    }
}
