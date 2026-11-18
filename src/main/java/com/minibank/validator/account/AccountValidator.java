package com.minibank.validator.account;

import com.minibank.dto.account.OpenAccountRequest;

public interface AccountValidator {

    void validateRequest(OpenAccountRequest request);
}
