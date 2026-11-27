package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class InvalidJsonStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof HttpMessageNotReadableException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                400,
                "Invalid JSON request body"
        );
    }
}
