package com.minibank.validator.account;

import com.minibank.dto.account.request.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class PositiveClientIdAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (request.clientId() <= 0) {
            throw new InvalidRequestException("ClientId must be positive");
        }
    }
}
