package com.minibank.validator.account;

import com.minibank.dto.account.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (request.clientId() == null) {
            throw new InvalidRequestException("clientId is required");
        }

        if (request.accountNumber() == null) {
            throw new InvalidRequestException("accountNumber is required");
        }
    }
}
