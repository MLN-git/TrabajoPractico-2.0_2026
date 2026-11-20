package Persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {
    private static final String URL = "jdbc:mysql://172.16.1.64:3306/midb";
    private static final String USER = "bdd1";
    private static final String PASSWORD = "bdd1";

    private ConexionDB() {
    }

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
