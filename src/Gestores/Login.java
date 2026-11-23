package Gestores;

import Models.Usuario;
import Persistence.UsuarioDAO;

import java.sql.SQLException;

public class Login {
    public Login() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        try {
            Usuario usuario = login(usuarioDAO);
            new Menu(new Gestora(usuario)).mostrarMenu();
        } catch (SQLException e) {
            System.out.println("Error al obtener el usuario: " + e.getMessage());
        }
    }

    public static Usuario login(UsuarioDAO usuarioDAO) throws SQLException {
        while (true) {
            String nombre = menu.Verificador.leerNombre("Usuario: ");
            String contrasenia = menu.Verificador.leerTexto("Contraseña: ");
            Usuario u = usuarioDAO.obtenerUsuarioPorNombre(nombre);
            if (u !=  null && u.getContrasenia().equals(contrasenia)) {
                return u;
            }
            System.out.println("Usuario no encontrado o contraseña incorrecta");
        }
    }

    public static Usuario register(UsuarioDAO usuarioDAO) throws SQLException {
        while (true) {
            String nombre = menu.Verificador.leerNombre("Usuario: ");
            String contrasenia = menu.Verificador.leerTexto("Contrasenia: ");

        }
    }
}
