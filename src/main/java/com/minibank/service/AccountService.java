package com.minibank.service;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.exception.custom_exceptions.AccountAlreadyExistsException;
import com.minibank.exception.custom_exceptions.AccountNotFoundException;
import com.minibank.mapper.AccountMapper;
import com.minibank.model.Account;
import com.minibank.model.Client;
import com.minibank.model.Transaction;
import com.minibank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private final ClientService clientService;

    public AccountService(ClientService clientService) {
        this.clientService = clientService;
    }

    private final AccountMapper accountMapper = new AccountMapper();

    // Открытие аккаунта (счета)
    public Account openAccount(Long clientId, String accountNumber) {
        Client client = clientService.getClientEntity(clientId);
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

    // Получение счетов клиента
    public List<AccountResponse> getAccountsByClientId(Long clientId) {
        return accountRepository.findAllByClientId(clientId)
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    // Получение счетов уже загруженного клиента (циклическая зависимость)
    public List<AccountResponse> getAccountsForClient(Client client) {
        return client.getAccounts()
                .stream()
                .map(accountMapper::toResponse)
                .toList();
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
