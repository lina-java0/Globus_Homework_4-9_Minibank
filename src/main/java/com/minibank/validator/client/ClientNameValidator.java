package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class ClientNameValidator implements PostClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (!request.name().matches(".*\\p{L}.*")) {
            throw new InvalidRequestException("Name must contain at least one letter");
        }
    }
}
