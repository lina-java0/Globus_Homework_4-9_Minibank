package com.minibank.mapper.transaction;

import com.minibank.dto.transaction.response.TransactionResponse;
import com.minibank.model.Transaction;

public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp()
        );
    }
}
