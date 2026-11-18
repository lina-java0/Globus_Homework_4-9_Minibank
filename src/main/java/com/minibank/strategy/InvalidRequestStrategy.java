package com.minibank.strategy;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.exception.custom_exceptions.InvalidRequestException;

public class InvalidRequestStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof InvalidRequestException;
    }

    @Override
    public HttpErrorResponse handle(Exception exception) {
        return new HttpErrorResponse(
                400,
                exception.getMessage()
        );
    }
}
