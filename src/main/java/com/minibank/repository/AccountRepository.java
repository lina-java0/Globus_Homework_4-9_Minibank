package com.minibank.repository;

import com.minibank.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AccountRepository {

    private final ConnectionFactory connectionFactory;

    public AccountRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Account save(Long clientId, String accountNumber) {
        String sql = """
            INSERT INTO accounts (client_id, account_number, balance)
            VALUES (?, ?, ?)
            RETURNING id
            """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, clientId);
            statement.setString(2, accountNumber);
            statement.setBigDecimal(3, BigDecimal.ZERO);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Account(
                            resultSet.getLong("id"),
                            clientId,
                            accountNumber,
                            BigDecimal.ZERO
                    );
                }
                throw new SQLException("Failed to retrieve account ID");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to open account", e);
        }
    }

    public Optional<Account> findById(Long accountId) {
        String sql = """
            SELECT id, client_id, account_number, balance
            FROM accounts
            WHERE id = ?
            """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Account(
                            resultSet.getLong("id"),
                            resultSet.getLong("client_id"),
                            resultSet.getString("account_number"),
                            resultSet.getBigDecimal("balance")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve account", e);
        }
    }

    public boolean updateBalance(Long accountId, BigDecimal balance) {
        String sql = """
            UPDATE accounts
            SET balance = ?
            WHERE id = ?
            """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, balance);
            statement.setLong(2, accountId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update account balance", e);
        }
    }
}
