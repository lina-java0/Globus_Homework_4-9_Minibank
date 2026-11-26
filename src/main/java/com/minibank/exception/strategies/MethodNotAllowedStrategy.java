package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.custom_exceptions.MethodNotAllowedException;
import org.springframework.stereotype.Component;

@Component
public class MethodNotAllowedStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof MethodNotAllowedException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                405,
                ex.getMessage()
        );
    }
}