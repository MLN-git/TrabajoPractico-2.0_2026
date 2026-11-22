package Persistence;

import Enums.ETipoMateria;
import Interfaces.ICursable;
import Models.Materia;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MateriaDAO {
    public void guardarMateria(Materia<? extends ICursable> materia) throws SQLException {
        String sql = "INSERT INTO materias (id, nombre, tipo_materia) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, materia.getId().toString());
            ps.setString(2, materia.getNombre());
            ps.setString(3, materia.getTipoMateria().name());
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
        String sql = "SELECT id, nombre, tipo_materia FROM materias WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Materia<>(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            leerTipoMateria(rs.getString("tipo_materia"))
                    );
                }
                return null;
            }
        }
    }

    public void guardarTodos(Collection<? extends Materia<? extends ICursable>> materias)
            throws SQLException {
        String sql = "INSERT INTO materias (id, nombre, tipo_materia) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (Materia<? extends ICursable> materia : materias) {
                    ps.setString(1, materia.getId().toString());
                    ps.setString(2, materia.getNombre());
                    ps.setString(3, materia.getTipoMateria().name());
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
        String sql = "SELECT id, nombre, tipo_materia FROM materias ORDER BY nombre";
        List<Materia<ICursable>> materias = new ArrayList<>();

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                materias.add(new Materia<>(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("nombre"),
                        leerTipoMateria(rs.getString("tipo_materia"))
                ));
            }
        }
        return materias;
    }

    public Materia<ICursable> obtenerCompletaPorId(UUID id) throws SQLException {
        Materia<ICursable> materia = obtenerMateriaPorId(id);
        if (materia == null) {
            return null;
        }

        if (materia.getTipoMateria() == ETipoMateria.PARTICULAR) {
            materia.getICursables().addAll(new ClaseIndividualDAO().listarPorMateria(id));
        } else if (materia.getTipoMateria() == ETipoMateria.COMISIONADA) {
            materia.getICursables().addAll(new ComisionDAO().listarPorMateria(id));
        }
        return materia;
    }

    public List<Materia<ICursable>> listarCompletas() throws SQLException {
        List<Materia<ICursable>> materiasCompletas = new ArrayList<>();
        for (Materia<ICursable> materia : listar()) {
            materiasCompletas.add(obtenerCompletaPorId(materia.getId()));
        }
        return materiasCompletas;
    }

    private ETipoMateria leerTipoMateria(String tipo) {
        if ("INDIVIDUAL".equals(tipo)) {
            return ETipoMateria.PARTICULAR;
        }
        return ETipoMateria.valueOf(tipo);
    }
}
