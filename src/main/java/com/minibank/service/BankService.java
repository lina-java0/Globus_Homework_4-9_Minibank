package com.minibank.service;

import com.minibank.dto.account.response.AccountResponse;
import com.minibank.dto.client.response.ClientResponse;
import com.minibank.exception.custom_exceptions.*;
import com.minibank.mapper.account.AccountMapper;
import com.minibank.mapper.client.ClientMapper;
import com.minibank.model.Account;
import com.minibank.model.Client;
import com.minibank.model.Transaction;
import jakarta.persistence.*;
import org.hibernate.exception.ConstraintViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * MiniBank — "MVP", который наспех собрал предыдущий разработчик:
 * вся логика в одном классе, все данные — в HashMap, персистентности
 * нет вообще (перезапуск процесса — и всё пропало).
 * <p>
 * ПРОБЛЕМА (найдёшь и исправишь, когда дойдёт очередь — модуль 7.1,
 * когда появится @Transactional): в transfer() проверка существования
 * счёта-получателя происходит ПОСЛЕ списания денег со счёта-
 * отправителя. Если счёт-получатель не существует, деньги уже спишутся
 * с первого счёта и "исчезнут" — их некуда вернуть, потому что метод
 * прерывается исключением до зачисления.
 * <p>
 * Эта проблема не решается на HashMap "в лоб" без искусственных
 * костылей (можно вручную откатывать баланс в catch, но это не
 * настоящее решение) — она по-настоящему решается только когда
 * появится транзакционность (сначала в БД — ACID, модуль 4, а затем
 * в коде — @Transactional, модуль 7.1). До этого момента баг просто
 * существует и это нормально: то, что появится в модуле 7.1, — как
 * раз тот инструмент, который проблему полностью закрывает.
 */
public class BankService {

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("minibank");

    private final ClientMapper clientMapper = new ClientMapper(new AccountMapper());
    private final AccountMapper accountMapper = new AccountMapper();

    // Регистрация клиента
    public Client registerClient(String name, String email) {

        EntityManager entityManager = EMF.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();

            Client client = new Client(name, email);

            entityManager.persist(client);
            transaction.commit();
            return client;
        } catch (RuntimeException ex) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            // Нюанс
            if (ex instanceof PersistenceException
                    && ex.getCause() instanceof ConstraintViolationException constraintEx
                    && "23505".equals(constraintEx.getSQLException().getSQLState())) {

                throw new EmailAlreadyExistsException("Client with this email already exists: " + email);
            }
            throw new IllegalStateException("Failed to register client", ex);
        }
    }

    // Открытие аккаунта (счета)
    public Account openAccount(Long clientId, String accountNumber) {

        EntityManager entityManager = EMF.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();

            Client client = entityManager.find(Client.class, clientId);

            if (client == null) {
                throw new ClientNotFoundException("Client not found: " + clientId);
            }

            Account account = new Account(client, accountNumber, BigDecimal.ZERO);
            client.addAccount(account);

            entityManager.persist(account);
            transaction.commit();
            return account;
        } catch (ClientNotFoundException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {

            if (transaction.isActive()) {
                transaction.rollback();
            }
            // Обрати внимание!!!
            if (ex instanceof PersistenceException
                    && ex.getCause() instanceof ConstraintViolationException constraintEx
                    && "23505".equals(constraintEx.getSQLException().getSQLState())) {

                throw new AccountAlreadyExistsException("Account number already exists: " + accountNumber);
            }
            throw new IllegalStateException("Failed to open account", ex);
        }
    }

    // Получение аккаунта по id
    public AccountResponse getAccount(Long accountId) {

        EntityManager entityManager = EMF.createEntityManager();

        try (entityManager) {
            Account foundAccount = entityManager.find(Account.class, accountId);

            if (foundAccount == null) {
                throw new AccountNotFoundException("Account not found: " + accountId);
            }

            return accountMapper.toResponse(foundAccount);
        } catch (AccountNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to retrieve account", ex);
        }
    }

    // Получение клиента по id
    public ClientResponse getClient(Long clientId) {

        EntityManager entityManager = EMF.createEntityManager();

        try (entityManager) {
            Client foundClient = entityManager.find(Client.class, clientId);

            if (foundClient == null) {
                throw new ClientNotFoundException("Client not found: " + clientId);
            }

            return clientMapper.toResponse(foundClient);
        } catch (ClientNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to retrieve client", ex);
        }
    }

    // Пополнение аккаунта (счета)
    public void deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        AccountResponse account = getAccount(accountId);
        BigDecimal newBalance = account.balance().add(amount);
        updateBalance(accountId, newBalance);
        recordTransaction(accountId, amount, "DEPOSIT");
    }

    // Перевод средств
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        AccountResponse from = getAccount(fromAccountId);

        // Списываем деньги ДО того, как проверили существование счёта-
        // получателя — если toAccountId не существует, деньги уже
        // списаны и потеряны
        if (from.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds on account " + fromAccountId);
        }

        // Списываем деньги до проверки существования счёта получателя
        updateBalance(
                fromAccountId,
                from.balance().subtract(amount)
        );

        recordTransaction(fromAccountId, amount.negate(), "TRANSFER_OUT");

        AccountResponse to = getAccount(toAccountId); // если бросит исключение — деньги уже пропали

        updateBalance(
                toAccountId,
                to.balance().add(amount)
        );

        recordTransaction(toAccountId, amount, "TRANSFER_IN");
    }

    // Запись транзакции
    private void recordTransaction(Long accountId, BigDecimal amount, String type) {

        EntityManager entityManager = EMF.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();

            Account account = entityManager.find(Account.class, accountId);

            if (account == null) {
                throw new AccountNotFoundException("Account not found: " + accountId);
            }

            Transaction bankTransaction = new Transaction(
                    account,
                    amount,
                    type,
                    LocalDateTime.now());

            entityManager.persist(bankTransaction);
            transaction.commit();
        } catch (AccountNotFoundException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new IllegalStateException("Failed to record transaction", ex);
        }
    }

    // Изменить баланс
    private void updateBalance(Long accountId, BigDecimal balance) {

        EntityManager entityManager = EMF.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();

            Account account = entityManager.find(Account.class, accountId);

            if (account == null) {
                throw new AccountNotFoundException("Account not found: " + accountId);
            }

            account.setBalance(balance);

            transaction.commit();

        } catch (AccountNotFoundException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new IllegalStateException("Failed to update account balance", ex);
        }
    }
}
