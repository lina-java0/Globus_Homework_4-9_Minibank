package com.minibank.validator.account;

import com.minibank.dto.account.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class AccountNumberBlankAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (request.accountNumber().isBlank()) {
            throw new InvalidRequestException("accountNumber must not be blank");
        }
    }
}
