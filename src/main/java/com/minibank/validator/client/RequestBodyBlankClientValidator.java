package com.minibank.validator.client;

import com.minibank.dto.client.request.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyBlankClientValidator implements PostClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request.name().isBlank()) {
            throw new InvalidRequestException("Name must not be blank");
        }

        if (request.email().isBlank()) {
            throw new InvalidRequestException("Email must not be blank");
        }
    }
}
