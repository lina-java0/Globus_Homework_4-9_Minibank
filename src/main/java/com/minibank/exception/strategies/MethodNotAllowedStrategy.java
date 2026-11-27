package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@Component
@Order(1)
public class MethodNotAllowedStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception ex) {
        return ex instanceof HttpRequestMethodNotSupportedException;
    }

    @Override
    public ErrorResponse handle(Exception ex) {
        return new ErrorResponse(
                405,
                ex.getMessage()
        );
    }
}