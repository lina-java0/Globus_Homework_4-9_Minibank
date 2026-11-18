package com.minibank;

import com.minibank.exception.custom_exceptions.*;

import java.math.BigDecimal;
import java.sql.*;

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

    // Константы подключения
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/minibank";
    private static final String DB_USERNAME = "minibank_user";
    private static final String DB_PASSWORD = "postgres";

    // Метод подключения
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                DB_USERNAME,
                DB_PASSWORD
        );
    }

    // Регистрация клиента
    public Client registerClient(String name, String email) {
        String sql = """
                INSERT INTO clients (name, email)
                VALUES (?, ?)
                RETURNING id
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    return new Client(
                            id,
                            name,
                            email);
                }
                throw new SQLException("Failed to retrieve client ID");
            }
        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                throw new EmailAlreadyExistsException("Client with this email already exists: " + email);
            }

            throw new IllegalStateException("Failed to register client", e);
        }
    }

    public Account openAccount(Long clientId, String accountNumber) {
        getClient(clientId);

        String sql = """
            INSERT INTO accounts (client_id, account_number, balance)
            VALUES (?, ?, ?)
            RETURNING id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, clientId);
            statement.setString(2, accountNumber);
            statement.setBigDecimal(3, BigDecimal.ZERO);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");

                    return new Account(
                            id,
                            clientId,
                            accountNumber,
                            BigDecimal.ZERO
                    );
                }
                throw new SQLException("Failed to retrieve account ID");
            }
        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                throw new AccountAlreadyExistsException("Account number already exists: " + accountNumber);
            }

            throw new IllegalStateException("Failed to open account", e);
        }
    }

    public Account getAccount(Long accountId) {
        String sql = """
            SELECT id, client_id, account_number, balance
            FROM accounts
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Account(
                            resultSet.getLong("id"),
                            resultSet.getLong("client_id"),
                            resultSet.getString("account_number"),
                            resultSet.getBigDecimal("balance")
                    );
                }
                throw new AccountNotFoundException("Account not found: " + accountId);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve account", e);
        }
    }

    public Client getClient(Long clientId) {
        String sql = """
            SELECT id, name, email
            FROM clients
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, clientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Client(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email")
                    );
                }
                throw new ClientNotFoundException("Client not found: " + clientId);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve client", e);
        }
    }

    public void deposit(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        Account account = getAccount(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        updateBalance(accountId, newBalance);
        recordTransaction(accountId, amount, "DEPOSIT");
    }

    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account from = getAccount(fromAccountId);

        // Списываем деньги ДО того, как проверили существование счёта-
        // получателя — если toAccountId не существует, деньги уже
        // списаны и потеряны
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds on account " + fromAccountId);
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
        String sql = """
            INSERT INTO transactions (account_id, amount, type)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, accountId);
            statement.setBigDecimal(2, amount);
            statement.setString(3, type);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to record transaction", e);
        }
    }

    private void updateBalance(Long accountId, BigDecimal balance) {
        String sql = """
            UPDATE accounts
            SET balance = ?
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, balance);
            statement.setLong(2, accountId);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new AccountNotFoundException("Account not found: " + accountId);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update account balance", e);
        }
    }
}
