package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestNullClientValidator implements ClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }
    }
}
