package com.minibank.strategy;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;

public class MethodNotAllowedStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof MethodNotAllowedException;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                405,
                ex.getMessage()
        );
    }
}