package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/gestion_scolaire";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "root";

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
            } catch (SQLException e) {
                System.err.println("[DB] Erreur fermeture : " + e.getMessage());
            }
        }
    }
}
