package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyBlankClientValidator implements ClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request.name().isBlank()) {
            throw new InvalidRequestException("name must not be blank");
        }

        if (request.email().isBlank()) {
            throw new InvalidRequestException("email must not be blank");
        }
    }
}
