package com.minibank.validator.account;

import com.minibank.dto.account.request.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class RequestNullAccountValidator implements AccountValidator {

    @Override
    public void validateRequest(OpenAccountRequest request) {

        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }
    }
}
