package com.medicore.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
"jdbc:mysql://localhost:3306/medicore_hms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";

    // নতুন password
    private static final String PASSWORD = "root123";

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver পাওয়া যায়নি।", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}