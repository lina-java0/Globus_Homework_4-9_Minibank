package com.minibank.exception.strategies;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.InsufficientFundsException;

public class InsufficientFundsStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof InsufficientFundsException;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                409,
                ex.getMessage()
        );
    }
}
