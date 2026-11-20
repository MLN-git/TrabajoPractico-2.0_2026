package Models;

import Interfaces.ICursable;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class Materia <T extends ICursable>{
    private UUID id;
    private String nombre;
    private Institucion institucion;
    private HashSet<T> iCursables;

    public Materia(String nombre, Institucion institucion) {
        this.nombre = nombre;
        this.institucion = institucion;
        this.id = UUID.randomUUID();
        iCursables = new HashSet<T>();
    }

    public Materia(UUID id, String nombre, Institucion institucion) {
        this.id = id;
        this.nombre = nombre;
        this.institucion = institucion;
        this.iCursables = new HashSet<>();
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Materia<?> materia)) return false;
        return Objects.equals(id, materia.id);
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

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    public HashSet<T> getICursables() {
        return iCursables;
    }

    public void setICursables(HashSet<T> aulas) {
        iCursables = aulas;
    }
}
