package com.minibank.validator.transfer;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestNullTransferValidator implements TransferValidator {

    @Override
    public void validateRequest(TransferRequest request) {

        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }
    }
}
