package com.minibank.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransferRepository {

    private final ConnectionFactory connectionFactory;

    public TransferRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void save(Long accountId, BigDecimal amount, String type) {
        String sql = """
            INSERT INTO transactions (account_id, amount, type)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, accountId);
            statement.setBigDecimal(2, amount);
            statement.setString(3, type);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to record transaction", e);
        }
    }
}
