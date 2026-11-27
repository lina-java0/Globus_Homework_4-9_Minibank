package com.minibank.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Константы подключения
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/minibank";
    private static final String DB_USERNAME = "minibank_user";
    private static final String DB_PASSWORD = "postgres";

    // Метод подключения
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                DB_USERNAME,
                DB_PASSWORD
        );
    }
}
