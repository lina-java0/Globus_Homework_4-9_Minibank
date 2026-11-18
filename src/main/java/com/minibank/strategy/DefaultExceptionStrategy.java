package com.minibank.strategy;

import com.minibank.dto.error.HttpErrorResponse;

public class DefaultExceptionStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception exception) {
        return true;
    }

    @Override
    public HttpErrorResponse handle(Exception exception) {
        return new HttpErrorResponse(
                500,
                "Internal server error"
        );
    }
}
