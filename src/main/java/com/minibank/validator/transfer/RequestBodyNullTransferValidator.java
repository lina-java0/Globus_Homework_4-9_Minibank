package com.minibank.validator.transfer;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class RequestBodyNullTransferValidator implements TransferValidator {

    @Override
    public void validateRequest(TransferRequest request) {
        if (request.fromAccountId() == null) {
            throw new InvalidRequestException("FromAccountId is required");
        }

        if (request.toAccountId() == null) {
            throw new InvalidRequestException("ToAccountId is required");
        }

        if (request.amount() == null) {
            throw new InvalidRequestException("Amount is required");
        }
    }
}
