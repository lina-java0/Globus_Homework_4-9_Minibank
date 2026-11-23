package com.minibank.validator.client;

import com.minibank.dto.client.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class ClientEmailValidator implements PostClientValidator {

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (!request.email().matches(EMAIL_PATTERN)) {
            throw new InvalidRequestException("Invalid email format");
        }
    }
}
