package com.minibank.validator.transfer;

import com.minibank.dto.transfer.TransferRequest;

public interface TransferValidator {

    void validateRequest(TransferRequest request);
}
