package com.minibank.validator.account;

import com.minibank.dto.account.request.OpenAccountRequest;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class AccountNumberValidator implements AccountValidator {

    private static final String ACCOUNT_NUMBER_REGEX = "^ACC-\\d+$";

    @Override
    public void validateRequest(OpenAccountRequest request) {
        if (!request.accountNumber().matches(ACCOUNT_NUMBER_REGEX)) {
            throw new InvalidRequestException("Invalid account number format");
        }
    }
}
