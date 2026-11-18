package Persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/asistencia";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private ConexionDB() {
    }

    public static Connection abrir() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new IllegalStateException(
                    "Configurá las variables de entorno DB_USER y DB_PASSWORD");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
