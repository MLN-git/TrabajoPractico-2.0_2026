package Models;

import Interfaces.ICursable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

public class Clase implements ICursable {
    private Date fechaYHora;
    private String nombre;
    private HashSet<Alumno> alumnos;

    public Clase(Date fechaYHora, String nombre) {
        this.fechaYHora = fechaYHora;
        this.nombre = nombre;
        this.alumnos = new HashSet<>();
    }



    @Override
    public Boolean agregarAlumno(Alumno alumno) {
        return alumnos.add(alumno);
    }

    @Override
    public Boolean eliminarAlumno(Alumno alumno) {
        return alumnos.remove(alumno);
    }

    @Override
    public String listarAlumnos() {
        StringBuilder sb = new StringBuilder();
        for (Alumno alumno : alumnos) {
            sb.append(alumno.getId());
            sb.append("\n");
        }
        return sb.toString();
    }


    public Date getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(Date fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Clase clase)) return false;
        return Objects.equals(fechaYHora, clase.fechaYHora);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fechaYHora);
    }
}
