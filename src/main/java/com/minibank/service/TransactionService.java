package com.minibank.service;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.exception.custom_exceptions.InsufficientFundsException;
import com.minibank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    // Перевод средств
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        AccountResponse fromAccount = accountService.getAccount(fromAccountId);

        // Списываем деньги ДО того, как проверили существование счёта-
        // получателя — если toAccountId не существует, деньги уже
        // списаны и потеряны
        if (fromAccount.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds on account " + fromAccountId);
        }

        // Списываем деньги до проверки существования аккаунта получателя
        accountService.updateBalance(
                fromAccountId,
                fromAccount.balance().subtract(amount)
        );

        accountService.recordTransaction(
                fromAccountId,
                amount.negate(),
                "TRANSFER_OUT"
        );

        AccountResponse toAccount = accountService.getAccount(toAccountId); // если бросит исключение — деньги уже пропали

        accountService.updateBalance(
                toAccountId,
                toAccount.balance().add(amount)
        );

        accountService.recordTransaction(
                toAccountId, amount,
                "TRANSFER_IN");
    }
}
