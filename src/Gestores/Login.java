package Gestores;

import Models.Usuario;
import Persistence.UsuarioDAO;

import java.sql.SQLException;

public class Login {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();


    public Usuario pantallaInicial(){
        while(true){
            System.out.println("---BIENVENIDO---\n");
            System.out.println("1 - Iniciar Sesion");
            System.out.println("2 - Registrarse");
            System.out.println("3 - Salir\n");

            switch (menu.Verificador.leerNumeroPositivoMinMax(1, 3)) {
                case 1: {
                    Usuario usuario = iniciarSesion();
                    if (usuario != null) {
                        return usuario;
                    }
                    break;
                }
                case 2:
                    registrar();
                    break;
                case 3:
                    return null;
            }

        }
    }

    private void registrar() {
        String nombre = menu.Verificador.leerTexto("Ingrese un nombre de usuario:");
        try {
            if (usuarioDAO.obtenerUsuarioPorNombre(nombre) != null) {
                System.out.println("Ese nombre de usuario ya existe.\n");
                return;
            }
            String contrasenia = menu.Verificador.leerTexto("Ingrese una contraseña:");
            usuarioDAO.guardarUsuario(new Usuario(nombre, contrasenia));
            System.out.println("Usuario creado. Ya puede iniciar sesión.\n");
        } catch (SQLException e) {
            System.out.println("No se pudo registrar el usuario: " + e.getMessage());
        }
    }

    public Usuario iniciarSesion()  {
        String nombre = menu.Verificador.leerTexto("Usuario: ");
        String password = menu.Verificador.leerTexto("Password: ");
        try{
            Usuario usuario = usuarioDAO.obtenerUsuarioPorNombre(nombre);
            if (usuario != null && usuario.getContrasenia().equals(password)) {
                System.out.println("\n Bienvenido");
                return usuario;
            }
            System.out.println("Usuario o contrasenia incorrectos");
        }catch (SQLException e){
            System.out.println("Error al consultar base de datos " + e.getMessage());
        }
        return null;
    }


}
