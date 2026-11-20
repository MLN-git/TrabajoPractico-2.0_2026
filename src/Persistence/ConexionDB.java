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

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS cursables (
                        id CHAR(36) PRIMARY KEY,
                        materia_id CHAR(36) NOT NULL,
                        tipo VARCHAR(30) NOT NULL,
                        nombre VARCHAR(100) NOT NULL,
                        CONSTRAINT fk_cursables_materia
                            FOREIGN KEY (materia_id) REFERENCES materias(id)
                            ON DELETE CASCADE
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS clases_individuales (
                        cursable_id CHAR(36) PRIMARY KEY,
                        fecha_y_hora DATETIME NOT NULL,
                        CONSTRAINT fk_clase_cursable
                            FOREIGN KEY (cursable_id) REFERENCES cursables(id)
                            ON DELETE CASCADE
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS comisiones (
                        cursable_id CHAR(36) PRIMARY KEY,
                        anio INT NOT NULL,
                        CONSTRAINT fk_comision_cursable
                            FOREIGN KEY (cursable_id) REFERENCES cursables(id)
                            ON DELETE CASCADE
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS cursable_alumnos (
                        cursable_id CHAR(36) NOT NULL,
                        alumno_id CHAR(36) NOT NULL,
                        PRIMARY KEY (cursable_id, alumno_id),
                        CONSTRAINT fk_cursable_alumnos_cursable
                            FOREIGN KEY (cursable_id) REFERENCES cursables(id)
                            ON DELETE CASCADE,
                        CONSTRAINT fk_cursable_alumnos_alumno
                            FOREIGN KEY (alumno_id) REFERENCES alumnos(id)
                            ON DELETE CASCADE
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS asistencia_sesiones (
                        id CHAR(36) PRIMARY KEY,
                        cursable_id CHAR(36) NOT NULL,
                        fecha DATE NOT NULL,
                        UNIQUE KEY uq_asistencia_cursable_fecha (cursable_id, fecha),
                        CONSTRAINT fk_asistencia_sesion_cursable
                            FOREIGN KEY (cursable_id) REFERENCES cursables(id)
                            ON DELETE CASCADE
                    )
                    """);

            sentencia.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS asistencia_detalle (
                        sesion_id CHAR(36) NOT NULL,
                        alumno_id CHAR(36) NOT NULL,
                        presente BOOLEAN NOT NULL,
                        PRIMARY KEY (sesion_id, alumno_id),
                        CONSTRAINT fk_asistencia_detalle_sesion
                            FOREIGN KEY (sesion_id) REFERENCES asistencia_sesiones(id)
                            ON DELETE CASCADE,
                        CONSTRAINT fk_asistencia_detalle_alumno
                            FOREIGN KEY (alumno_id) REFERENCES alumnos(id)
                            ON DELETE CASCADE
                    )
                    """);
        }

        inicializada = true;
    }
}
