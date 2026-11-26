package com.minibank.validator.transaction;

import com.minibank.dto.transaction.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class PositiveAccountIdTransferValidator implements TransferValidator {

    @Override
    public void validateRequest(TransferRequest request) {
        if (request.fromAccountId() <= 0) {
            throw new InvalidRequestException("FromAccountId must be positive");
        }

        if (request.toAccountId() <= 0) {
            throw new InvalidRequestException("ToAccountId must be positive");
        }
    }
}
