package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullClientValidator implements ClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request.name() == null || request.name().isBlank()
                || request.email() == null || request.email().isBlank()) {
            throw new InvalidRequestException("All fields are required");
        }
    }
}
