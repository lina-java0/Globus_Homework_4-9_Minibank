package com.minibank.service;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.exception.custom_exceptions.AccountAlreadyExistsException;
import com.minibank.exception.custom_exceptions.AccountNotFoundException;
import com.minibank.exception.custom_exceptions.ClientNotFoundException;
import com.minibank.mapper.AccountMapper;
import com.minibank.model.Account;
import com.minibank.model.Client;
import com.minibank.model.Transaction;
import com.minibank.repository.AccountRepository;
import com.minibank.repository.ClientRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountService {

    private final AccountMapper accountMapper = new AccountMapper();

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    public AccountService(
            ClientRepository clientRepository,
            AccountRepository accountRepository
    ) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    // Открытие аккаунта (счета)
    @Transactional
    public Account openAccount(Long clientId, String accountNumber) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found: " + clientId));
        Account account = new Account(client, accountNumber, BigDecimal.ZERO);
        client.addAccount(account);

        try {
            return accountRepository.save(account);
        } catch (DataIntegrityViolationException ex) {
            throw new AccountAlreadyExistsException("Account number already exists: " + accountNumber);
        }
    }

    // Получение аккаунта по id
    public AccountResponse getAccount(Long accountId) {
        Account foundAccount = getAccountEntity(accountId);
        return accountMapper.toResponse(foundAccount);
    }

    // Изменение баланса
    void updateBalance(Long accountId, BigDecimal balance) {
        Account account = getAccountEntity(accountId);
        account.setBalance(balance);
        accountRepository.save(account);
    }

    // Запись транзакции в аккаунт
    void recordTransaction(Long accountId, BigDecimal amount, String type) {
        Account account = getAccountEntity(accountId);

        Transaction bankTransaction = new Transaction(
                account,
                amount,
                type,
                LocalDateTime.now());
        account.addTransaction(bankTransaction);

        accountRepository.save(account);
    }

    // Получение сущности аккаунта для внутренних операций
    private Account getAccountEntity(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
    }
}
