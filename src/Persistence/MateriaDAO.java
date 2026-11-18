package Persistence;

import Interfaces.ICursable;
import Models.Institucion;
import Models.Materia;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MateriaDAO {
    public void guardarMateria(Materia<? extends ICursable> materia) throws SQLException {
        String sql = "INSERT INTO materias "
                + "(id, nombre, institucion_nombre, institucion_domicilio) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, materia.getId().toString());
            ps.setString(2, materia.getNombre());
            if (materia.getInstitucion() == null) {
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(3, materia.getInstitucion().getNombre());
                ps.setString(4, materia.getInstitucion().getDomicilio());
            }
            ps.executeUpdate();
        }
    }

    public void eliminarMateria(UUID id) throws SQLException {
        String sql = "DELETE FROM materias WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    public Materia<ICursable> obtenerMateriaPorId(UUID id) throws SQLException {
        String sql = "SELECT * FROM materias WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreInstitucion = rs.getString("institucion_nombre");
                    Institucion institucion = nombreInstitucion == null ? null : new Institucion(
                            nombreInstitucion,
                            rs.getString("institucion_domicilio")
                    );
                    return new Materia<>(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            institucion
                    );
                }
                return null;
            }
        }
    }

    public void guardarTodos(Collection<? extends Materia<? extends ICursable>> materias)
            throws SQLException {
        String sql = "INSERT INTO materias "
                + "(id, nombre, institucion_nombre, institucion_domicilio) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (Materia<? extends ICursable> materia : materias) {
                    ps.setString(1, materia.getId().toString());
                    ps.setString(2, materia.getNombre());
                    if (materia.getInstitucion() == null) {
                        ps.setNull(3, Types.VARCHAR);
                        ps.setNull(4, Types.VARCHAR);
                    } else {
                        ps.setString(3, materia.getInstitucion().getNombre());
                        ps.setString(4, materia.getInstitucion().getDomicilio());
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

    public List<Materia<ICursable>> listar() throws SQLException {
        String sql = "SELECT * FROM materias ORDER BY nombre";
        List<Materia<ICursable>> materias = new ArrayList<>();

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombreInstitucion = rs.getString("institucion_nombre");
                Institucion institucion = nombreInstitucion == null ? null : new Institucion(
                        nombreInstitucion,
                        rs.getString("institucion_domicilio")
                );
                materias.add(new Materia<>(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("nombre"),
                        institucion
                ));
            }
        }
        return materias;
    }
}
