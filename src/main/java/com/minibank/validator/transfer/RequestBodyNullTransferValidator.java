package com.minibank.validator.transfer;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullTransferValidator implements TransferValidator {

    @Override
    public void validateRequest(TransferRequest request) {
        if (request.fromAccountId() == null
                || request.toAccountId() == null
                || request.amount() == null) {
            throw new InvalidRequestException("All fields are required");
        }
    }
}
