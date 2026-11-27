package com.minibank.dto.transfer;

import java.math.BigDecimal;

public record TransferRequest(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount
) {
}
