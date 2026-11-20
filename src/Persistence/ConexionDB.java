package Persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConexionDB {
    private static final String BASE_DATOS = "TPFinal_2026_ManuIgna";
    private static final String URL_SERVIDOR = "jdbc:mysql://172.16.1.64:3306/";
    private static final String URL = URL_SERVIDOR + BASE_DATOS;
    private static final String USER = "bdd1";
    private static final String PASSWORD = "bdd1";
    private static boolean inicializada;

    private ConexionDB() {
    }

    public static Connection abrir() throws SQLException {
        inicializar();
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static synchronized void inicializar() throws SQLException {
        if (inicializada) {
            return;
        }

        try (Connection conexionServidor =
                     DriverManager.getConnection(URL_SERVIDOR, USER, PASSWORD);
             Statement sentencia = conexionServidor.createStatement()) {
            sentencia.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS `" + BASE_DATOS + "`");
        }

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS alumnos (
                        id CHAR(36) PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellido VARCHAR(100) NOT NULL,
                        anio_ingreso INT NULL
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id CHAR(36) PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL UNIQUE,
                        contrasenia VARCHAR(255) NOT NULL
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS materias (
                        id CHAR(36) PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        tipo_materia VARCHAR(30) NOT NULL
                    )
                    """);
        }

        inicializada = true;
    }
}
