package Persistence;

import Exceptions.ElementoRepetidoException;
import Models.Alumno;
import Models.Comision;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ComisionDAO {
    public void guardarComision(Comision comision, UUID materiaId, UUID usuarioId)
            throws SQLException {
        String sqlCursable = """
                INSERT INTO cursables (id, materia_id, tipo, nombre) VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE materia_id = VALUES(materia_id),
                    tipo = VALUES(tipo), nombre = VALUES(nombre)
                """;
        String sqlComision = """
                INSERT INTO comisiones (cursable_id, anio) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE anio = VALUES(anio)
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psCursable = conexion.prepareStatement(sqlCursable);
                 PreparedStatement psComision = conexion.prepareStatement(sqlComision)) {
                verificarCursableDisponible(conexion, comision.getId(), usuarioId);
                verificarMateriaUsuario(conexion, materiaId, usuarioId);
                psCursable.setString(1, comision.getId().toString());
                psCursable.setString(2, materiaId.toString());
                psCursable.setString(3, "COMISIONADA");
                psCursable.setString(4, comision.getNombreElegible());
                psCursable.executeUpdate();

                psComision.setString(1, comision.getId().toString());
                psComision.setInt(2, comision.getAnio());
                psComision.executeUpdate();
                conexion.commit();
            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        }
    }

    public void eliminarComision(UUID id, UUID usuarioId) throws SQLException {
        String sql = """
                DELETE c FROM cursables c
                JOIN materias m ON m.id = c.materia_id
                WHERE c.id = ? AND c.tipo = 'COMISIONADA' AND m.usuario_id = ?
                """;
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, usuarioId.toString());
            ps.executeUpdate();
        }
    }

    public void actualizarNombre(UUID id, String nombre, UUID usuarioId) throws SQLException {
        String sql = """
                UPDATE cursables c
                JOIN materias m ON m.id = c.materia_id
                SET c.nombre = ?
                WHERE c.id = ? AND c.tipo = 'COMISIONADA' AND m.usuario_id = ?
                """;
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, id.toString());
            ps.setString(3, usuarioId.toString());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                verificarPropiedadCursable(conexion, id, usuarioId);
            }
        }
    }

    public Comision obtenerComisionPorId(UUID id, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT c.id, c.nombre, co.anio
                FROM cursables c
                JOIN comisiones co ON co.cursable_id = c.id
                JOIN materias m ON m.id = c.materia_id
                WHERE c.id = ? AND c.tipo = 'COMISIONADA' AND m.usuario_id = ?
                """;
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, usuarioId.toString());
            Comision comision;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                comision = new Comision(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("nombre"),
                        rs.getInt("anio")
                );
            }
            cargarAlumnos(conexion, comision, usuarioId);
            cargarAsistencias(conexion, comision, usuarioId);
            return comision;
        }
    }

    public List<Comision> listarPorMateria(UUID materiaId, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT c.id
                FROM cursables c
                JOIN materias m ON m.id = c.materia_id
                WHERE c.materia_id = ? AND c.tipo = 'COMISIONADA'
                    AND m.usuario_id = ?
                ORDER BY c.nombre
                """;
        List<UUID> ids = new ArrayList<>();
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, materiaId.toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(UUID.fromString(rs.getString("id")));
                }
            }
        }

        List<Comision> comisiones = new ArrayList<>();
        for (UUID id : ids) {
            Comision comision = obtenerComisionPorId(id, usuarioId);
            if (comision != null) {
                comisiones.add(comision);
            }
        }
        return comisiones;
    }

    public void guardarAlumnos(
            UUID comisionId, Collection<Alumno> alumnos, UUID usuarioId) throws SQLException {
        String borrar = """
                DELETE ca FROM cursable_alumnos ca
                JOIN cursables c ON c.id = ca.cursable_id
                JOIN materias m ON m.id = c.materia_id
                WHERE ca.cursable_id = ? AND c.tipo = 'COMISIONADA'
                    AND m.usuario_id = ?
                """;
        String insertar = """
                INSERT INTO cursable_alumnos (cursable_id, alumno_id)
                SELECT ?, a.id
                FROM alumnos a
                JOIN cursables c ON c.id = ?
                JOIN materias m ON m.id = c.materia_id
                WHERE a.id = ? AND a.usuario_id = ? AND c.tipo = 'COMISIONADA'
                    AND m.usuario_id = ?
                """;
        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try {
                verificarPropiedadCursable(conexion, comisionId, usuarioId);
                try (PreparedStatement psBorrar = conexion.prepareStatement(borrar)) {
                    psBorrar.setString(1, comisionId.toString());
                    psBorrar.setString(2, usuarioId.toString());
                    psBorrar.executeUpdate();
                }
                try (PreparedStatement psInsertar = conexion.prepareStatement(insertar)) {
                    for (Alumno alumno : alumnos) {
                        psInsertar.setString(1, comisionId.toString());
                        psInsertar.setString(2, comisionId.toString());
                        psInsertar.setString(3, alumno.getId().toString());
                        psInsertar.setString(4, usuarioId.toString());
                        psInsertar.setString(5, usuarioId.toString());
                        if (psInsertar.executeUpdate() != 1) {
                            throw new SQLException(
                                    "El alumno no pertenece al usuario o la comisión no existe");
                        }
                    }
                }
                conexion.commit();
            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        }
    }

    public void guardarAsistencia(
            Comision comision, LocalDate fecha, UUID usuarioId) throws SQLException {
        Collection<Alumno> presentes = comision.getAsistencias().get(fecha);
        if (presentes == null) {
            throw new IllegalArgumentException("No hay una asistencia registrada para " + fecha);
        }
        guardarAsistencia(comision.getId(), fecha, comision.getElegibles(), presentes, usuarioId);
    }

    public void guardarAsistencia(
            UUID comisionId, LocalDate fecha, Collection<Alumno> elegibles,
            Collection<Alumno> presentes, UUID usuarioId) throws SQLException {
        String crearSesion = """
                INSERT INTO asistencia_sesiones (id, cursable_id, fecha) VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE fecha = VALUES(fecha)
                """;
        String obtenerSesion = """
                SELECT id FROM asistencia_sesiones WHERE cursable_id = ? AND fecha = ?
                """;
        String borrarDetalle = "DELETE FROM asistencia_detalle WHERE sesion_id = ?";
        String insertarDetalle = """
                INSERT INTO asistencia_detalle (sesion_id, alumno_id, presente)
                SELECT ?, a.id, ?
                FROM alumnos a
                JOIN cursable_alumnos ca ON ca.alumno_id = a.id
                JOIN cursables c ON c.id = ca.cursable_id
                JOIN materias m ON m.id = c.materia_id
                WHERE a.id = ? AND a.usuario_id = ? AND c.id = ?
                    AND c.tipo = 'COMISIONADA' AND m.usuario_id = ?
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try {
                verificarPropiedadCursable(conexion, comisionId, usuarioId);
                try (PreparedStatement ps = conexion.prepareStatement(crearSesion)) {
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, comisionId.toString());
                    ps.setDate(3, Date.valueOf(fecha));
                    ps.executeUpdate();
                }

                String sesionId;
                try (PreparedStatement ps = conexion.prepareStatement(obtenerSesion)) {
                    ps.setString(1, comisionId.toString());
                    ps.setDate(2, Date.valueOf(fecha));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("No se encontró la sesión de asistencia creada");
                        }
                        sesionId = rs.getString("id");
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(borrarDetalle)) {
                    ps.setString(1, sesionId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conexion.prepareStatement(insertarDetalle)) {
                    for (Alumno alumno : elegibles) {
                        ps.setString(1, sesionId);
                        ps.setBoolean(2, presentes.contains(alumno));
                        ps.setString(3, alumno.getId().toString());
                        ps.setString(4, usuarioId.toString());
                        ps.setString(5, comisionId.toString());
                        ps.setString(6, usuarioId.toString());
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "El alumno no pertenece al usuario o no está asignado a la comisión");
                        }
                    }
                }
                conexion.commit();
            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
                throw e;
            }
        }
    }

    private void cargarAlumnos(
            Connection conexion, Comision comision, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT a.id, a.nombre, a.apellido, a.anio_ingreso
                FROM cursable_alumnos ca JOIN alumnos a ON a.id = ca.alumno_id
                WHERE ca.cursable_id = ? AND a.usuario_id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, comision.getId().toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        comision.agregarAlumno(new Alumno(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getObject("anio_ingreso", Integer.class)
                        ));
                    } catch (ElementoRepetidoException e) {
                        throw new SQLException(
                                "La base contiene un alumno duplicado para la comisión "
                                        + comision.getId(),
                                e
                        );
                    }
                }
            }
        }
    }

    private void cargarAsistencias(
            Connection conexion, Comision comision, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT s.fecha, a.id AS alumno_id, a.nombre, a.apellido, a.anio_ingreso
                FROM asistencia_sesiones s
                LEFT JOIN asistencia_detalle d
                    ON d.sesion_id = s.id AND d.presente = TRUE
                LEFT JOIN alumnos a ON a.id = d.alumno_id AND a.usuario_id = ?
                WHERE s.cursable_id = ?
                ORDER BY s.fecha
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuarioId.toString());
            ps.setString(2, comision.getId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    java.util.HashSet<Alumno> presentes = comision.getAsistencias()
                            .computeIfAbsent(fecha, dia -> new java.util.HashSet<>());
                    String alumnoId = rs.getString("alumno_id");
                    if (alumnoId != null) {
                        presentes.add(new Alumno(
                                UUID.fromString(alumnoId),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getObject("anio_ingreso", Integer.class)
                        ));
                    }
                }
            }
        }
    }

    private void verificarMateriaUsuario(
            Connection conexion, UUID materiaId, UUID usuarioId) throws SQLException {
        String sql = "SELECT 1 FROM materias WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, materiaId.toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La materia no pertenece al usuario");
                }
            }
        }
    }

    private void verificarPropiedadCursable(
            Connection conexion, UUID cursableId, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT 1
                FROM cursables c
                JOIN materias m ON m.id = c.materia_id
                WHERE c.id = ? AND c.tipo = 'COMISIONADA' AND m.usuario_id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cursableId.toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La comisión no pertenece al usuario");
                }
            }
        }
    }

    private void verificarCursableDisponible(
            Connection conexion, UUID cursableId, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT m.usuario_id
                FROM cursables c
                JOIN materias m ON m.id = c.materia_id
                WHERE c.id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cursableId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && !usuarioId.toString().equals(rs.getString("usuario_id"))) {
                    throw new SQLException("El identificador de comisión pertenece a otro usuario");
                }
            }
        }
    }
}
