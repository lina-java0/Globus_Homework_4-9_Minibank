package com.minibank.validator.transfer;

import com.minibank.dto.transfer.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
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
