package com.minibank.validator.transaction;

import com.minibank.dto.transaction.TransferRequest;

public interface TransferValidator {

    void validateRequest(TransferRequest request);
}
