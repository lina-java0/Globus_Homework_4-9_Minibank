package com.minibank.validator.transfer;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullTransferValidator implements TransferValidator {

    @Override
    public void validateRequest(TransferRequest request) {
        if (request.fromAccountId() == null) {
            throw new InvalidRequestException("fromAccountId is required");
        }

        if (request.toAccountId() == null) {
            throw new InvalidRequestException("toAccountId is required");
        }

        if (request.amount() == null) {
            throw new InvalidRequestException("amount is required");
        }
    }
}
