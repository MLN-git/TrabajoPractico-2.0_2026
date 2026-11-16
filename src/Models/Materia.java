package Models;

import java.util.HashSet;

public class Materia <T>{
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


}
