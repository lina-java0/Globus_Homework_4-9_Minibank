package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.custom_exceptions.InsufficientFundsException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(7)
public class InsufficientFundsStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof InsufficientFundsException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                409,
                ex.getMessage()
        );
    }
}
