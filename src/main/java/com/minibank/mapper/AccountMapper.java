package com.minibank.mapper;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.model.Account;

public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }
}
