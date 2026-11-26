package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import org.springframework.stereotype.Component;

@Component
public class DefaultExceptionStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return true;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                500,
                "Internal server error"
        );
    }
}
