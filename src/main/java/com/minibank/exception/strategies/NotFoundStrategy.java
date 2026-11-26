package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.custom_exceptions.AccountNotFoundException;
import com.minibank.exception.custom_exceptions.ClientNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class NotFoundStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof AccountNotFoundException
                || ex instanceof ClientNotFoundException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                404,
                ex.getMessage());
    }
}
