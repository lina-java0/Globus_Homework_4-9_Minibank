package com.minibank.validator.client;

import com.minibank.dto.client.request.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class ClientNameValidator implements PostClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (!request.name().matches(".*\\p{L}.*")) {
            throw new InvalidRequestException("Name must contain at least one letter");
        }
    }
}
