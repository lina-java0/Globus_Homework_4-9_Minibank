package com.minibank.exception.strategies;

import com.minibank.dto.error.HttpErrorResponse;

public interface ExceptionHandlerStrategy {

    // Умеет ли стратегия обрабатывать переданное исключение
    boolean supports(Exception ex);

    // Формирует HTTP-ответ
    HttpErrorResponse handle(Exception ex);
}
