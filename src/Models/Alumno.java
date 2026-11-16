package Models;

public class Alumno {
    private String nombre;
    private String apellido;
    private Integer anioIngreso;    // No obligatorio
    private Integer id;
    private static Integer idTotal=0;

    public Alumno(String nombre, String apellido, Integer anioIngreso) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.anioIngreso = anioIngreso;
        this.id = idTotal++;
    }


}
