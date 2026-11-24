import Gestores.Gestora;
import Gestores.Login;
import Models.Usuario;

import java.sql.SQLException;

void main() {
    try {
        Usuario user = new Login().pantallaInicial();

        if (user != null){
            Gestora sesion = new Gestora(user);
        }

    } catch (SQLException e) {
        System.out.println("Error al iniciar el Sesion: "  + e.getMessage());
    }
}
