package Persistence;

import Models.Alumno;
import Models.Comision;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ComisionDAO {
    public void guardarComision(Comision comision, UUID materiaId) throws SQLException {
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
                psCursable.setString(1, comision.getId().toString());
                psCursable.setString(2, materiaId.toString());
                psCursable.setString(3, "COMISIONADA");
                psCursable.setString(4, comision.getNombre());
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

    public void eliminarComision(UUID id) throws SQLException {
        String sql = "DELETE FROM cursables WHERE id = ? AND tipo = 'COMISIONADA'";
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    public Comision obtenerComisionPorId(UUID id) throws SQLException {
        String sql = """
                SELECT c.id, c.nombre, co.anio
                FROM cursables c
                JOIN comisiones co ON co.cursable_id = c.id
                WHERE c.id = ? AND c.tipo = 'COMISIONADA'
                """;
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
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
            cargarAlumnos(conexion, comision);
            cargarAsistencias(conexion, comision);
            return comision;
        }
    }

    public List<Comision> listarPorMateria(UUID materiaId) throws SQLException {
        String sql = "SELECT id FROM cursables "
                + "WHERE materia_id = ? AND tipo = 'COMISIONADA' ORDER BY nombre";
        List<UUID> ids = new ArrayList<>();
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, materiaId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(UUID.fromString(rs.getString("id")));
                }
            }
        }

        List<Comision> comisiones = new ArrayList<>();
        for (UUID id : ids) {
            Comision comision = obtenerComisionPorId(id);
            if (comision != null) {
                comisiones.add(comision);
            }
        }
        return comisiones;
    }

    public void guardarAlumnos(UUID comisionId, Collection<Alumno> alumnos) throws SQLException {
        String borrar = "DELETE FROM cursable_alumnos WHERE cursable_id = ?";
        String insertar = "INSERT INTO cursable_alumnos (cursable_id, alumno_id) VALUES (?, ?)";
        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psBorrar = conexion.prepareStatement(borrar);
                 PreparedStatement psInsertar = conexion.prepareStatement(insertar)) {
                psBorrar.setString(1, comisionId.toString());
                psBorrar.executeUpdate();
                for (Alumno alumno : alumnos) {
                    psInsertar.setString(1, comisionId.toString());
                    psInsertar.setString(2, alumno.getId().toString());
                    psInsertar.addBatch();
                }
                psInsertar.executeBatch();
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

    public void guardarAsistencia(Comision comision, LocalDate fecha) throws SQLException {
        Collection<Alumno> presentes = comision.getAsistencias().get(fecha);
        if (presentes == null) {
            throw new IllegalArgumentException("No hay una asistencia registrada para " + fecha);
        }
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
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexion.prepareStatement(crearSesion)) {
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, comision.getId().toString());
                    ps.setDate(3, Date.valueOf(fecha));
                    ps.executeUpdate();
                }

                String sesionId;
                try (PreparedStatement ps = conexion.prepareStatement(obtenerSesion)) {
                    ps.setString(1, comision.getId().toString());
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
                    for (Alumno alumno : comision.getElegibles()) {
                        ps.setString(1, sesionId);
                        ps.setString(2, alumno.getId().toString());
                        ps.setBoolean(3, presentes.contains(alumno));
                        ps.addBatch();
                    }
                    ps.executeBatch();
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

    private void cargarAlumnos(Connection conexion, Comision comision) throws SQLException {
        String sql = """
                SELECT a.id, a.nombre, a.apellido, a.anio_ingreso
                FROM cursable_alumnos ca JOIN alumnos a ON a.id = ca.alumno_id
                WHERE ca.cursable_id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, comision.getId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comision.agregarAlumno(new Alumno(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getObject("anio_ingreso", Integer.class)
                    ));
                }
            }
        }
    }

    private void cargarAsistencias(Connection conexion, Comision comision) throws SQLException {
        String sql = """
                SELECT s.fecha, a.id AS alumno_id, a.nombre, a.apellido, a.anio_ingreso
                FROM asistencia_sesiones s
                LEFT JOIN asistencia_detalle d
                    ON d.sesion_id = s.id AND d.presente = TRUE
                LEFT JOIN alumnos a ON a.id = d.alumno_id
                WHERE s.cursable_id = ?
                ORDER BY s.fecha
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, comision.getId().toString());
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
}
