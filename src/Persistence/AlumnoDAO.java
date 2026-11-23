package Persistence;

import Models.Alumno;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AlumnoDAO {
    public void guardarAlumno(Alumno alumno, UUID usuarioId) throws SQLException {
        String sql = """
                INSERT INTO alumnos (id, usuario_id, nombre, apellido, anio_ingreso)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, alumno.getId().toString());
            ps.setString(2, usuarioId.toString());
            ps.setString(3, alumno.getNombre());
            ps.setString(4, alumno.getApellido());
            if (alumno.getAnioIngreso() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, alumno.getAnioIngreso());
            }
            ps.executeUpdate();
        }
    }

    public void eliminarAlumno(UUID alumnoId, UUID usuarioId) throws SQLException {
        String sql = "DELETE FROM alumnos WHERE id = ? AND usuario_id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, alumnoId.toString());
            ps.setString(2, usuarioId.toString());
            ps.executeUpdate();
        }
    }

    public Alumno obtenerAlumnoPorId(UUID alumnoId, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT id, nombre, apellido, anio_ingreso
                FROM alumnos
                WHERE id = ? AND usuario_id = ?
                """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, alumnoId.toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Alumno(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getObject("anio_ingreso", Integer.class)
                    );
                }
                return null;
            }
        }
    }

    public void guardarTodos(Collection<Alumno> alumnos, UUID usuarioId) throws SQLException {
        String sql = """
                INSERT INTO alumnos (id, usuario_id, nombre, apellido, anio_ingreso)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (Alumno alumno : alumnos) {
                    ps.setString(1, alumno.getId().toString());
                    ps.setString(2, usuarioId.toString());
                    ps.setString(3, alumno.getNombre());
                    ps.setString(4, alumno.getApellido());
                    if (alumno.getAnioIngreso() == null) {
                        ps.setNull(5, Types.INTEGER);
                    } else {
                        ps.setInt(5, alumno.getAnioIngreso());
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
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

    public List<Alumno> listar(UUID usuarioId) throws SQLException {
        String sql = """
                SELECT id, nombre, apellido, anio_ingreso
                FROM alumnos
                WHERE usuario_id = ?
                ORDER BY apellido, nombre
                """;
        List<Alumno> alumnos = new ArrayList<>();

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alumnos.add(new Alumno(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getObject("anio_ingreso", Integer.class)
                    ));
                }
            }
        }
        return alumnos;
    }
}
