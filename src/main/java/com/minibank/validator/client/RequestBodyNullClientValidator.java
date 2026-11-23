package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullClientValidator implements PostClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request.name() == null) {
            throw new InvalidRequestException("Name is required");
        }

        if (request.email() == null) {
            throw new InvalidRequestException("Email is required");
        }
    }
}
