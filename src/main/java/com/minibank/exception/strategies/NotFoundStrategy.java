package com.minibank.exception.strategies;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.AccountNotFoundException;
import com.minibank.exception.custom_exceptions.ClientNotFoundException;
import com.minibank.exception.custom_exceptions.RouteNotFoundException;

public class NotFoundStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof AccountNotFoundException
                || ex instanceof ClientNotFoundException
                || ex instanceof RouteNotFoundException;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                404,
                ex.getMessage());
    }
}
