package com.minibank.exception;

import com.minibank.dto.error.ErrorResponse;
import com.minibank.exception.strategies.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExceptionMapper {

    private final List<ExceptionHandlerStrategy> strategyList;

    public ExceptionMapper(List<ExceptionHandlerStrategy> strategyList) {
        this.strategyList = strategyList;
    }

    public ErrorResponse map(Exception ex) {
        return strategyList.stream()
                .filter(strategy -> strategy.supports(ex))
                .findFirst()
                .orElseThrow().handle(ex);
    }
}
