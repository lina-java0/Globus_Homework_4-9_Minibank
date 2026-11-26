package com.minibank.dto.transaction;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount
) {
}
