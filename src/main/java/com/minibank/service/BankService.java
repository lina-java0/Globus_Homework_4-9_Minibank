package com.minibank.service;

import com.minibank.model.Account;
import com.minibank.model.Client;
import com.minibank.repository.AccountRepository;
import com.minibank.repository.ClientRepository;
import com.minibank.repository.TransferRepository;

import java.math.BigDecimal;

/**
 * MiniBank — "MVP", который наспех собрал предыдущий разработчик:
 * вся логика в одном классе, все данные — в HashMap, персистентности
 * нет вообще (перезапуск процесса — и всё пропало).
 *
 * ПРОБЛЕМА (найдёшь и исправишь, когда дойдёт очередь — модуль 7.1,
 * когда появится @Transactional): в transfer() проверка существования
 * счёта-получателя происходит ПОСЛЕ списания денег со счёта-
 * отправителя. Если счёт-получатель не существует, деньги уже спишутся
 * с первого счёта и "исчезнут" — их некуда вернуть, потому что метод
 * прерывается исключением до зачисления.
 *
 * Эта проблема не решается на HashMap "в лоб" без искусственных
 * костылей (можно вручную откатывать баланс в catch, но это не
 * настоящее решение) — она по-настоящему решается только когда
 * появится транзакционность (сначала в БД — ACID, модуль 4, а затем
 * в коде — @Transactional, модуль 7.1). До этого момента баг просто
 * существует и это нормально: то, что появится в модуле 7.1, — как
 * раз тот инструмент, который проблему полностью закрывает.
 */
public class BankService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public BankService(
            ClientRepository clientRepository,
            AccountRepository accountRepository,
            TransferRepository transferRepository
    ) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    // Регистрация клиента
    public Client registerClient(String name, String email) {
        return clientRepository.save(name, email);
    }

    // Открытие аккаунта (счета)
    public Account openAccount(Long clientId, String accountNumber) {
        getClient(clientId);

        return accountRepository.save(clientId, accountNumber);
    }

    // Получение аккаунта по id
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
    }

    // Получение клиента по id
    public Client getClient(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
    }

    // Пополнение аккаунта
    public void deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        Account account = getAccount(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);

        updateBalance(accountId, newBalance);
        recordTransaction(accountId, amount, "DEPOSIT");
    }

    // Перевод между аккаунтами
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account from = getAccount(fromAccountId);

        // Списываем деньги ДО того, как проверили существование счёта-
        // получателя — если toAccountId не существует, деньги уже
        // списаны и потеряны
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds on account " + fromAccountId);
        }

        // Списываем деньги до проверки существования счёта получателя
        updateBalance(
                fromAccountId,
                from.getBalance().subtract(amount)
        );

        recordTransaction(fromAccountId, amount.negate(), "TRANSFER_OUT");

        Account to = getAccount(toAccountId); // если бросит исключение — деньги уже пропали

        updateBalance(
                toAccountId,
                to.getBalance().add(amount)
        );

        recordTransaction(toAccountId, amount, "TRANSFER_IN");
    }

    private void recordTransaction(Long accountId, BigDecimal amount, String type) {
        transferRepository.save(accountId, amount, type);
    }

    private void updateBalance(Long accountId, BigDecimal balance) {
        boolean updated = accountRepository.updateBalance(accountId, balance);

        if (!updated) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
    }
}
