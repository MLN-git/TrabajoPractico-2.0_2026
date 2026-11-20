package Models;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Alumno {
    private String nombre;
    private String apellido;
    private Integer anioIngreso;    // No obligatorio
    private UUID id;


    public Alumno(String nombre, String apellido, Integer anioIngreso) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.anioIngreso = anioIngreso;
        this.id = UUID.randomUUID();
    }

    public Alumno(UUID id, String nombre, String apellido, Integer anioIngreso) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.anioIngreso = anioIngreso;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Alumno alumno)) return false;
        return Objects.equals(id, alumno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getAnioIngreso() {
        return anioIngreso;
    }

    public void setAnioIngreso(Integer anioIngreso) {
        this.anioIngreso = anioIngreso;
    }

    public String nombreCompleto(){
        return this.nombre + " " + this.apellido;
    }
}
