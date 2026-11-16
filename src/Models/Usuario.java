package Models;

public class Usuario {
    private String nombre;
    private String contraseña;
    private Integer id;
    private static Integer idTotal = 0;

    public Usuario(String nombre, String contraseña) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.id = idTotal++;
    }
}
