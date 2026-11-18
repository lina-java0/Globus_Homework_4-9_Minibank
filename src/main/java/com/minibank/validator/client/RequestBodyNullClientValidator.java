package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullClientValidator implements ClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request.name() == null) {
            throw new InvalidRequestException("name is required");
        }

        if (request.email() == null) {
            throw new InvalidRequestException("email is required");
        }
    }
}
