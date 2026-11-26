package com.minibank.validator.client;

import com.minibank.dto.client.request.RegisterClientRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class RequestNullClientValidator implements PostClientValidator {

    @Override
    public void validateRequest(RegisterClientRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }
    }
}
