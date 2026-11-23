package com.minibank.validator.account;

import com.minibank.dto.account.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class PositiveClientIdAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (request.clientId() <= 0) {
            throw new InvalidRequestException("ClientId must be positive");
        }
    }
}
