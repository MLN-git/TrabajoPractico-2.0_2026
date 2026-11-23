package Persistence;

import Enums.ETipoMateria;
import Models.Materia;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MateriaDAO {
    public void guardarMateria(Materia materia, UUID usuarioId) throws SQLException {
        String sql = """
                INSERT INTO materias (id, usuario_id, nombre, tipo_materia)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, materia.getId().toString());
            ps.setString(2, usuarioId.toString());
            ps.setString(3, materia.getNombre());
            ps.setString(4, materia.getTipoMateria().name());
            ps.executeUpdate();
        }
    }

    public void eliminarMateria(UUID id, UUID usuarioId) throws SQLException {
        String sql = "DELETE FROM materias WHERE id = ? AND usuario_id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, usuarioId.toString());
            ps.executeUpdate();
        }
    }

    public Materia obtenerMateriaPorId(UUID id, UUID usuarioId) throws SQLException {
        String sql = """
                SELECT id, nombre, tipo_materia
                FROM materias
                WHERE id = ? AND usuario_id = ?
                """;

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.setString(2, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Materia(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            leerTipoMateria(rs.getString("tipo_materia"))
                    );
                }
                return null;
            }
        }
    }

    public void guardarTodos(Collection<Materia> materias, UUID usuarioId) throws SQLException {
        String sql = """
                INSERT INTO materias (id, usuario_id, nombre, tipo_materia)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (Materia materia : materias) {
                    ps.setString(1, materia.getId().toString());
                    ps.setString(2, usuarioId.toString());
                    ps.setString(3, materia.getNombre());
                    ps.setString(4, materia.getTipoMateria().name());
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

    public List<Materia> listar(UUID usuarioId) throws SQLException {
        String sql = """
                SELECT id, nombre, tipo_materia
                FROM materias
                WHERE usuario_id = ?
                ORDER BY nombre
                """;
        List<Materia> materias = new ArrayList<>();

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuarioId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materias.add(new Materia(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            leerTipoMateria(rs.getString("tipo_materia"))
                    ));
                }
            }
        }
        return materias;
    }

    public Materia obtenerCompletaPorId(UUID id, UUID usuarioId) throws SQLException {
        Materia materia = obtenerMateriaPorId(id, usuarioId);
        if (materia == null) {
            return null;
        }

        if (materia.getTipoMateria() == ETipoMateria.PARTICULAR) {
            materia.getICursables().addAll(
                    new ClaseIndividualDAO().listarPorMateria(id, usuarioId));
        } else if (materia.getTipoMateria() == ETipoMateria.COMISIONADA) {
            materia.getICursables().addAll(new ComisionDAO().listarPorMateria(id, usuarioId));
        }
        return materia;
    }

    public List<Materia> listarCompletas(UUID usuarioId) throws SQLException {
        List<Materia> materiasCompletas = new ArrayList<>();
        for (Materia materia : listar(usuarioId)) {
            materiasCompletas.add(obtenerCompletaPorId(materia.getId(), usuarioId));
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
