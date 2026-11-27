package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.custom_exceptions.EmailAlreadyExistsException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class EmailAlreadyExistsStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof EmailAlreadyExistsException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                409,
                ex.getMessage());
    }
}
