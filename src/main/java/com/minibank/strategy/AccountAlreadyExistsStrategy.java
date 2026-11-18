package com.minibank.strategy;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.AccountAlreadyExistsException;

public class AccountAlreadyExistsStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof AccountAlreadyExistsException;
    }

    @Override
    public HttpErrorResponse handle(Exception exception) {
        return new HttpErrorResponse(
                409,
                exception.getMessage()
        );
    }
}
