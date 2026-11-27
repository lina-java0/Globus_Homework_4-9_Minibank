package com.minibank.exception.strategies;

import com.minibank.dto.error.ErrorResponse;

public interface ExceptionHandlerStrategy {

    // Умеет ли стратегия обрабатывать переданное исключение
    boolean supports(Exception ex);

    // Формирует HTTP-ответ
    ErrorResponse handle(Exception ex);
}
