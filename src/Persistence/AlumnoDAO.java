package Persistence;

import Models.Alumno;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AlumnoDAO {
    public void guardarAlumno(Alumno alumno) throws SQLException {
        String sql = """
        INSERT INTO alumnos (id, nombre, apellido, anio_ingreso)
        VALUES (?, ?, ?, ?)
        """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, alumno.getId().toString());
            ps.setString(2, alumno.getNombre());
            ps.setString(3, alumno.getApellido());

            if (alumno.getAnioIngreso() == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, alumno.getAnioIngreso());
            }

            ps.executeUpdate();
        }
    }

    public void eliminarAlumno(Alumno alumno) throws SQLException {
        String sql = "DELETE FROM alumnos WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, alumno.getId().toString());
            ps.executeUpdate();
        }
    }

    public Alumno obtenerAlumnoPorId(String id) throws SQLException {
        String sql = "SELECT * FROM alumnos WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            var rs = ps.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                Integer anioIngreso = rs.getInt("anio_ingreso");
                if (rs.wasNull()) {
                    anioIngreso = null;
                }
                Alumno alumno = new Alumno(nombre, apellido, anioIngreso);
                return alumno;
            } else {
                return null;
            }
        }
    }

    public void guardarTodos(Collection<Alumno> alumnos) throws SQLException {
        String sql = """
            INSERT INTO alumnos (id, nombre, apellido, anio_ingreso)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (Alumno alumno : alumnos) {
                    ps.setString(1, alumno.getId().toString());
                    ps.setString(2, alumno.getNombre());
                    ps.setString(3, alumno.getApellido());

                    if (alumno.getAnioIngreso() == null) {
                        ps.setNull(4, Types.INTEGER);
                    } else {
                        ps.setInt(4, alumno.getAnioIngreso());
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

    public List<Alumno> listar() throws SQLException {
        String sql = """
            SELECT id, nombre, apellido, anio_ingreso
            FROM alumnos
            ORDER BY apellido, nombre
            """;

        List<Alumno> alumnos = new ArrayList<>();

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alumnos.add(new Alumno(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getObject("anio_ingreso", Integer.class)
                ));
            }
        }
        return alumnos;
    }


}
