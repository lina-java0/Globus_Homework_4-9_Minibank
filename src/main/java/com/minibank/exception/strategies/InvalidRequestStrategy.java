package com.minibank.exception.strategies;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class InvalidRequestStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof InvalidRequestException;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                400,
                ex.getMessage()
        );
    }
}
