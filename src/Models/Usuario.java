package Models;

import java.util.UUID;

public class Usuario {
    private String nombre;
    private String contraseña;
    private UUID id;

    public Usuario(String nombre, String contraseña) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.id = UUID.randomUUID();
    }
}
