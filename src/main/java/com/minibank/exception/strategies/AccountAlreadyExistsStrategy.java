package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.custom_exceptions.AccountAlreadyExistsException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class AccountAlreadyExistsStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof AccountAlreadyExistsException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                409,
                ex.getMessage()
        );
    }
}
