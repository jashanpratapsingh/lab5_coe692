package com.mycompany.lab5.reservation.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String FILE = "/database.properties";

    public Connection getConnection() throws SQLException {
        Properties p = loadProperties();

        String host = resolve(p, "db.host", "RESERVATION_DB_HOST", "DB_HOST", "localhost");
        String port = resolve(p, "db.port", "RESERVATION_DB_PORT", "DB_PORT", "3306");
        String name = resolve(p, "db.name", "RESERVATION_DB_NAME", "DB_NAME", "Reservation_Rental_Lab5_DB");
        String user = resolve(p, "db.user", "RESERVATION_DB_USER", "DB_USER", "root");
        String pass = resolve(p, "db.password", "RESERVATION_DB_PASSWORD", "DB_PASSWORD", "root");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        return DriverManager.getConnection(url, user, pass);
    }

    private Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream(FILE)) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException ignored) {
        }
        return p;
    }

    private String resolve(Properties p, String key, String serviceEnv, String sharedEnv, String fallback) {
        String value = getenv(serviceEnv);
        if (value != null) return value;

        value = getenv(sharedEnv);
        if (value != null) return value;

        value = trimToNull(p.getProperty(key));
        if (value != null) return value;

        return fallback;
    }

    private String getenv(String key) {
        return trimToNull(System.getenv(key));
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
