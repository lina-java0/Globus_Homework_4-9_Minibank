package com.minibank.validator.account;

import com.minibank.dto.account.request.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class RequestBodyNullAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (request.clientId() == null) {
            throw new InvalidRequestException("ClientId is required");
        }

        if (request.accountNumber() == null) {
            throw new InvalidRequestException("AccountNumber is required");
        }
    }
}
