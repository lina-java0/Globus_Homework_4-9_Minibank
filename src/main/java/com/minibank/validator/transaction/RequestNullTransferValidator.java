package com.minibank.validator.transaction;

import com.minibank.dto.transaction.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class RequestNullTransferValidator implements TransferValidator {

    @Override
    public void validateRequest(TransferRequest request) {

        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }
    }
}
