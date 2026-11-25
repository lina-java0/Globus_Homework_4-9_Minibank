package com.minibank.validator.account;

import com.minibank.dto.account.request.OpenAccountRequest;

public interface AccountValidator {

    void validateRequest(OpenAccountRequest request);
}
