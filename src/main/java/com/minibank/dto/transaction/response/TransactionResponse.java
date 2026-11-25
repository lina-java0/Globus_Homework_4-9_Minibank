package com.minibank.dto.transaction.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long accountId,
        BigDecimal amount,
        String type,
        LocalDateTime timestamp
) {
}
