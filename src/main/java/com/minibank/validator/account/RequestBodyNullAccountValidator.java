package com.minibank.validator.account;

import com.minibank.dto.account.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (request.clientId() == null || request.clientId() <= 0) {
            throw new InvalidRequestException("Valid client ID is required");
        }

        if (request.accountNumber() == null
                || request.accountNumber().isBlank()) {
            throw new InvalidRequestException("Account number is required");
        }
    }
}
