package com.minibank.validator.transaction;

import com.minibank.dto.transaction.TransferRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AmountTransferValidator implements TransferValidator{

    @Override
    public void validateRequest(TransferRequest request) {
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Transfer amount must be greater than zero");
        }
    }
}
