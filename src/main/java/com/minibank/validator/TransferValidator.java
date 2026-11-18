package com.minibank.validator;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

import java.math.BigDecimal;

public class TransferValidator {

    public void validateRequest(TransferRequest request) {

        if (request == null) {
            throw new InvalidRequestException("Request body is required");
        }

        if (request.fromAccountId() == null
                || request.toAccountId() == null
                || request.amount() == null) {
            throw new InvalidRequestException("All fields are required");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Transfer amount must be greater than zero");
        }
    }
}
