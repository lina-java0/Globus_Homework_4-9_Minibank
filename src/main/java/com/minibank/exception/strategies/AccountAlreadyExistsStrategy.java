package com.minibank.exception.strategies;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.AccountAlreadyExistsException;

public class AccountAlreadyExistsStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof AccountAlreadyExistsException;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                409,
                ex.getMessage()
        );
    }
}
