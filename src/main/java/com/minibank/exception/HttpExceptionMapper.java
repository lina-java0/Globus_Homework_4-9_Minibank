package com.minibank.exception;

import com.minibank.dto.error.HttpErrorResponse;
import com.minibank.strategy.*;

import java.util.List;

public class HttpExceptionMapper {

    private final List<ExceptionHandlerStrategy> strategyList;

    public HttpExceptionMapper() {
        this.strategyList = List.of(
                new NotFoundStrategy(),
                new InsufficientFundsStrategy(),
                new InvalidRequestStrategy(),
                new MethodNotAllowedStrategy(),
                new DefaultExceptionStrategy());
    }

    public HttpErrorResponse map(Exception ex) {
        return strategyList.stream()
                .filter(strategy -> strategy.supports(ex))
                .findFirst()
                .orElseThrow().handle(ex);
    }
}
