import Gestores.Gestora;
import Gestores.Login;
import Models.Usuario;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Usuario user = new Login().pantallaInicial();

        if (user != null){
            Gestora sesion = new Gestora(user);
        }

    } catch (SQLException e) {
        System.out.println("Error al iniciar el Sesion: "  + e.getMessage());
    }
}
}
