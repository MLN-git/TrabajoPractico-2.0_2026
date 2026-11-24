package Persistence;

import Models.Alumno;
import Models.ClaseIndividual;
import Exceptions.ElementoRepetidoException;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ClaseIndividualDAO {
    public void guardarClase(ClaseIndividual clase, UUID materiaId, UUID usuarioId)
            throws SQLException {
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
                verificarCursableDisponible(conexion, clase.getId(), usuarioId);
                verificarMateriaUsuario(conexion, materiaId, usuarioId);
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

    public void eliminarClase(UUID id, UUID usuarioId) throws SQLException {
        String sql = """
                DELETE c FROM cursables c
                JOIN materias m ON m.id = c.materia_id
                WHERE c.id = ? AND c.tipo = 'INDIVIDUAL' AND m.usuario_id = ?
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
                WHERE c.id = ? AND c.tipo = 'INDIVIDUAL' AND m.usuario_id = ?
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

    public ClaseIndividual obtenerClasePorId(UUID id, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT c.id, c.nombre, ci.fecha_y_hora
                FROM cursables c
                JOIN clases_individuales ci ON ci.cursable_id = c.id
                JOIN materias m ON m.id = c.materia_id
                WHERE c.id = ? AND c.tipo = 'INDIVIDUAL' AND m.usuario_id = ?
                """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, usuarioId.toString());
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
            cargarAlumnos(conexion, clase, usuarioId);
            cargarAsistencia(conexion, clase, usuarioId);
            return clase;
        }
    }

    public List<ClaseIndividual> listarPorMateria(UUID materiaId, UUID usuarioId)
            throws SQLException {
        String sql = """
                SELECT c.id
                FROM cursables c
                JOIN materias m ON m.id = c.materia_id
                WHERE c.materia_id = ? AND c.tipo = 'INDIVIDUAL'
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

        List<ClaseIndividual> clases = new ArrayList<>();
        for (UUID id : ids) {
            ClaseIndividual clase = obtenerClasePorId(id, usuarioId);
            if (clase != null) {
                clases.add(clase);
            }
        }
        return clases;
    }

    public void guardarAlumnos(
            UUID claseId, Collection<Alumno> alumnos, UUID usuarioId) throws SQLException {
        String borrar = """
                DELETE ca FROM cursable_alumnos ca
                JOIN cursables c ON c.id = ca.cursable_id
                JOIN materias m ON m.id = c.materia_id
                WHERE ca.cursable_id = ? AND c.tipo = 'INDIVIDUAL' AND m.usuario_id = ?
                """;
        String insertar = """
                INSERT INTO cursable_alumnos (cursable_id, alumno_id)
                SELECT ?, a.id
                FROM alumnos a
                JOIN cursables c ON c.id = ?
                JOIN materias m ON m.id = c.materia_id
                WHERE a.id = ? AND a.usuario_id = ? AND c.tipo = 'INDIVIDUAL'
                    AND m.usuario_id = ?
                """;
        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try {
                verificarPropiedadCursable(conexion, claseId, usuarioId);
                try (PreparedStatement psBorrar = conexion.prepareStatement(borrar)) {
                    psBorrar.setString(1, claseId.toString());
                    psBorrar.setString(2, usuarioId.toString());
                    psBorrar.executeUpdate();
                }

                try (PreparedStatement psInsertar = conexion.prepareStatement(insertar)) {
                    for (Alumno alumno : alumnos) {
                        psInsertar.setString(1, claseId.toString());
                        psInsertar.setString(2, claseId.toString());
                        psInsertar.setString(3, alumno.getId().toString());
                        psInsertar.setString(4, usuarioId.toString());
                        psInsertar.setString(5, usuarioId.toString());
                        if (psInsertar.executeUpdate() != 1) {
                            throw new SQLException(
                                    "El alumno no pertenece al usuario o la clase no existe");
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

    public void guardarAsistencia(ClaseIndividual clase, UUID usuarioId) throws SQLException {
        if (!clase.isAsistenciaTomada()) {
            throw new IllegalStateException("Primero hay que tomar la asistencia de la clase");
        }
        LocalDate fecha = clase.getFechaYHora().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        guardarAsistencia(
                clase.getId(), fecha, clase.getElegibles(), clase.getAsistencia(), usuarioId);
    }

    public void guardarAsistencia(
            UUID cursableId, LocalDate fecha, Collection<Alumno> alumnos,
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
                    AND c.tipo = 'INDIVIDUAL' AND m.usuario_id = ?
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try {
                verificarPropiedadCursable(conexion, cursableId, usuarioId);
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
                        ps.setBoolean(2, presentes.contains(alumno));
                        ps.setString(3, alumno.getId().toString());
                        ps.setString(4, usuarioId.toString());
                        ps.setString(5, cursableId.toString());
                        ps.setString(6, usuarioId.toString());
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "El alumno no pertenece al usuario o no está asignado a la clase");
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
            Connection conexion, ClaseIndividual clase, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT a.id, a.nombre, a.apellido, a.anio_ingreso
                FROM cursable_alumnos ca JOIN alumnos a ON a.id = ca.alumno_id
                WHERE ca.cursable_id = ? AND a.usuario_id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, clase.getId().toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        clase.agregarAlumno(new Alumno(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getObject("anio_ingreso", Integer.class)
                        ));
                    } catch (ElementoRepetidoException e) {
                        throw new SQLException(
                                "La base contiene un alumno duplicado para la clase "
                                        + clase.getId(),
                                e
                        );
                    }
                }
            }
        }
    }

    private void cargarAsistencia(
            Connection conexion, ClaseIndividual clase, UUID usuarioId) throws SQLException {
        LocalDate fecha = clase.getFechaYHora().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        String sql = """
                SELECT a.id AS alumno_id, a.nombre, a.apellido, a.anio_ingreso
                FROM asistencia_sesiones s
                LEFT JOIN asistencia_detalle d
                    ON d.sesion_id = s.id AND d.presente = TRUE
                LEFT JOIN alumnos a ON a.id = d.alumno_id AND a.usuario_id = ?
                WHERE s.cursable_id = ? AND s.fecha = ?
                """;
        boolean asistenciaEncontrada = false;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuarioId.toString());
            ps.setString(2, clase.getId().toString());
            ps.setDate(3, Date.valueOf(fecha));
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
                WHERE c.id = ? AND c.tipo = 'INDIVIDUAL' AND m.usuario_id = ?
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cursableId.toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La clase no pertenece al usuario");
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
                    throw new SQLException("El identificador de clase pertenece a otro usuario");
                }
            }
        }
    }
}
