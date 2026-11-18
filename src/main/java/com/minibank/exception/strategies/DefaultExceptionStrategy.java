package com.minibank.exception.strategies;

import com.minibank.dto.error.HttpErrorResponse;

public class DefaultExceptionStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return true;
    }

    @Override
    public HttpErrorResponse handle(Exception ex) {
        return new HttpErrorResponse(
                500,
                "Internal server error"
        );
    }
}
