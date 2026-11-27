package com.minibank.repository;

import com.minibank.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClientRepository {

    private final ConnectionFactory connectionFactory;

    public ClientRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Client save(String name, String email) {
        String sql = """
                INSERT INTO clients (name, email)
                VALUES (?, ?)
                RETURNING id
                """;

        try (Connection connection = connectionFactory.getConnection();
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
            throw new IllegalStateException("Failed to register client", e);
        }
    }

    public Optional<Client> findById(Long clientId) {
        String sql = """
            SELECT id, name, email
            FROM clients
            WHERE id = ?
            """;

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, clientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Client(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve client", e);
        }
    }
}
