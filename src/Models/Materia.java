package Models;

import Interfaces.ICursable;

import java.util.HashSet;
import java.util.Objects;

public class Materia <T extends ICursable>{
    private Integer id;
    private static Integer idTotal=0;
    private String nombre;
    private Institucion institucion;
    private HashSet<T> Aulas;

    public Materia(String nombre, Institucion institucion) {
        this.nombre = nombre;
        this.institucion = institucion;
        this.id = idTotal++;
        Aulas = new HashSet<T>();
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

    public Integer getId() {
        return id;
    }

    public static Integer getIdTotal() {
        return idTotal;
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

    public HashSet<T> getAulas() {
        return Aulas;
    }

    public void setAulas(HashSet<T> aulas) {
        Aulas = aulas;
    }
}
