package com.minibank.service;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.exception.custom_exceptions.InsufficientFundsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountService accountService;

    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    // Перевод средств
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Сначала проверяем существование обоих счетов
        AccountResponse fromAccount = accountService.getAccount(fromAccountId);
        AccountResponse toAccount = accountService.getAccount(toAccountId);

        // Проверяем достаточность средств
        if (fromAccount.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds on account " + fromAccountId);
        }

        // Списываем деньги
        accountService.updateBalance(
                fromAccountId,
                fromAccount.balance().subtract(amount)
        );

        // Записываем транзакцию
        accountService.recordTransaction(
                fromAccountId,
                amount.negate(),
                "TRANSFER_OUT"
        );

        // Зачисляем деньги
        accountService.updateBalance(
                toAccountId,
                toAccount.balance().add(amount)
        );

        // Записываем транзакцию
        accountService.recordTransaction(
                toAccountId, amount,
                "TRANSFER_IN");
    }
}
