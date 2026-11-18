package com.minibank.strategy;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.AccountNotFoundException;
import com.minibank.exception.custom_exceptions.ClientNotFoundException;

public class NotFoundStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof AccountNotFoundException
                || ex instanceof ClientNotFoundException;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                404,
                ex.getMessage());
    }
}
