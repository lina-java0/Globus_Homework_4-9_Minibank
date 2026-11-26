package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.custom_exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class InvalidRequestStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof InvalidRequestException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                400,
                ex.getMessage()
        );
    }
}
