package Persistence;

import Models.Alumno;
import Models.ClaseIndividual;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ClaseIndividualDAO {
    public void guardarClase(ClaseIndividual clase, UUID materiaId) throws SQLException {
        String sqlCursable = """
                INSERT INTO cursables (id, materia_id, tipo, nombre) VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE materia_id = VALUES(materia_id),
                    tipo = VALUES(tipo), nombre = VALUES(nombre)
                """;
        String sqlClase = """
                INSERT INTO clases_individuales (cursable_id, fecha_y_hora) VALUES (?, ?)
                ON DUPLICATE KEY UPDATE fecha_y_hora = VALUES(fecha_y_hora)
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psCursable = conexion.prepareStatement(sqlCursable);
                 PreparedStatement psClase = conexion.prepareStatement(sqlClase)) {
                psCursable.setString(1, clase.getId().toString());
                psCursable.setString(2, materiaId.toString());
                psCursable.setString(3, "INDIVIDUAL");
                psCursable.setString(4, clase.getNombre());
                psCursable.executeUpdate();

                psClase.setString(1, clase.getId().toString());
                psClase.setTimestamp(2, new Timestamp(clase.getFechaYHora().getTime()));
                psClase.executeUpdate();
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

    public void eliminarClase(UUID id) throws SQLException {
        String sql = "DELETE FROM cursables WHERE id = ? AND tipo = 'INDIVIDUAL'";
        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    public ClaseIndividual obtenerClasePorId(UUID id) throws SQLException {
        String sql = """
                SELECT c.id, c.nombre, ci.fecha_y_hora
                FROM cursables c
                JOIN clases_individuales ci ON ci.cursable_id = c.id
                WHERE c.id = ? AND c.tipo = 'INDIVIDUAL'
                """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ClaseIndividual clase;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                clase = new ClaseIndividual(
                        UUID.fromString(rs.getString("id")),
                        rs.getTimestamp("fecha_y_hora"),
                        rs.getString("nombre")
                );
            }
            cargarAlumnos(conexion, clase);
            cargarAsistencia(conexion, clase);
            return clase;
        }
    }

    public List<ClaseIndividual> listarPorMateria(UUID materiaId) throws SQLException {
        String sql = "SELECT id FROM cursables "
                + "WHERE materia_id = ? AND tipo = 'INDIVIDUAL' ORDER BY nombre";
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

        List<ClaseIndividual> clases = new ArrayList<>();
        for (UUID id : ids) {
            ClaseIndividual clase = obtenerClasePorId(id);
            if (clase != null) {
                clases.add(clase);
            }
        }
        return clases;
    }

    public void guardarAlumnos(UUID claseId, Collection<Alumno> alumnos) throws SQLException {
        String borrar = "DELETE FROM cursable_alumnos WHERE cursable_id = ?";
        String insertar = "INSERT INTO cursable_alumnos (cursable_id, alumno_id) VALUES (?, ?)";

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement psBorrar = conexion.prepareStatement(borrar);
                 PreparedStatement psInsertar = conexion.prepareStatement(insertar)) {
                psBorrar.setString(1, claseId.toString());
                psBorrar.executeUpdate();

                for (Alumno alumno : alumnos) {
                    psInsertar.setString(1, claseId.toString());
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

    public void guardarAsistencia(ClaseIndividual clase) throws SQLException {
        if (!clase.isAsistenciaTomada()) {
            throw new IllegalStateException("Primero hay que tomar la asistencia de la clase");
        }
        LocalDate fecha = clase.getFechaYHora().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        guardarAsistencia(clase.getId(), fecha, clase.getElegibles(), clase.getAsistencia());
    }

    private void guardarAsistencia(
            UUID cursableId, LocalDate fecha, Collection<Alumno> alumnos,
            Collection<Alumno> presentes) throws SQLException {
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
                    ps.setString(2, cursableId.toString());
                    ps.setDate(3, Date.valueOf(fecha));
                    ps.executeUpdate();
                }

                String sesionId;
                try (PreparedStatement ps = conexion.prepareStatement(obtenerSesion)) {
                    ps.setString(1, cursableId.toString());
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
                    for (Alumno alumno : alumnos) {
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

    private void cargarAlumnos(Connection conexion, ClaseIndividual clase) throws SQLException {
        String sql = """
                SELECT a.id, a.nombre, a.apellido, a.anio_ingreso
                FROM cursable_alumnos ca JOIN alumnos a ON a.id = ca.alumno_id
                WHERE ca.cursable_id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, clase.getId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clase.agregarAlumno(new Alumno(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getObject("anio_ingreso", Integer.class)
                    ));
                }
            }
        }
    }

    private void cargarAsistencia(Connection conexion, ClaseIndividual clase) throws SQLException {
        LocalDate fecha = clase.getFechaYHora().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        String sql = """
                SELECT a.id AS alumno_id, a.nombre, a.apellido, a.anio_ingreso
                FROM asistencia_sesiones s
                LEFT JOIN asistencia_detalle d
                    ON d.sesion_id = s.id AND d.presente = TRUE
                LEFT JOIN alumnos a ON a.id = d.alumno_id
                WHERE s.cursable_id = ? AND s.fecha = ?
                """;
        boolean asistenciaEncontrada = false;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, clase.getId().toString());
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    asistenciaEncontrada = true;
                    String alumnoId = rs.getString("alumno_id");
                    if (alumnoId != null) {
                        clase.getAsistencia().add(new Alumno(
                                UUID.fromString(alumnoId),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getObject("anio_ingreso", Integer.class)
                        ));
                    }
                }
            }
        }
        if (asistenciaEncontrada) {
            clase.marcarAsistenciaTomada();
        }
    }
}
