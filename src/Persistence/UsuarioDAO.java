package Persistence;

import Models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioDAO {
    public void guardarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (id, nombre, contrasenia) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getId().toString());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getContrasenia());
            ps.executeUpdate();
        }
    }

    public void eliminarUsuario(UUID id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            ps.executeUpdate();
        }
    }

    public Usuario obtenerUsuarioPorId(UUID id) throws SQLException {
        String sql = "SELECT id, nombre, contrasenia FROM usuarios WHERE id = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            rs.getString("contrasenia")
                    );
                }
                return null;
            }
        }
    }

    public Usuario obtenerUsuarioPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, contrasenia FROM usuarios WHERE nombre = ?";

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("nombre"),
                            rs.getString("contrasenia")
                    );
                }
                return null;
            }
        }
    }

    public void guardarTodos(Collection<Usuario> usuarios) throws SQLException {
        String sql = "INSERT INTO usuarios (id, nombre, contrasenia) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (Usuario usuario : usuarios) {
                    ps.setString(1, usuario.getId().toString());
                    ps.setString(2, usuario.getNombre());
                    ps.setString(3, usuario.getContrasenia());
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

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id, nombre, contrasenia FROM usuarios ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexion = ConexionDB.abrir();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("nombre"),
                        rs.getString("contrasenia")
                ));
            }
        }
        return usuarios;
    }
}
