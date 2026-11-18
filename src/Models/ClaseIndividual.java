package Models;

import Exceptions.AlumnosVacioException;
import Interfaces.ICursable;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import static Gestores.Menu.consultarAlumno;

public class ClaseIndividual implements ICursable {
    private Date fechaYHora;
    private String nombre;
    private HashSet<Alumno> alumnos;
    private HashSet<Alumno> asistencia;

    public ClaseIndividual(Date fechaYHora, String nombre) {
        this.fechaYHora = fechaYHora;
        this.nombre = nombre;
        this.alumnos = new HashSet<>();
    }


    @Override
    public void tomarAsistencia() throws AlumnosVacioException {
        if (alumnos.isEmpty()){
            throw new AlumnosVacioException("No hay alumnos asignados a la clase");
        }
        if (this.asistencia == null) {
            this.asistencia = new HashSet<>();
            for (Alumno alumno : this.alumnos) {
                if (consultarAlumno(alumno)){
                    this.asistencia.add(alumno);
                }
            }
        }
    }

    @Override
    public String listarAsistencias() throws AlumnosVacioException {
        if (this.asistencia == null || asistencia.isEmpty()){
            throw new AlumnosVacioException("Nadie asistió todavía");
        }
        StringBuilder s = new StringBuilder();
        for  (Alumno alumno : this.asistencia) {
            s.append(alumno.nombreCompleto());
            s.append("\n");
        }
        return s.toString();
    }

    @Override
    public Boolean agregarPresente(Alumno alumno) {
        return asistencia.add(alumno);
    }

    @Override
    public Boolean eliminarPresente(Alumno alumno) {
        return asistencia.remove(alumno);
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
        if (!(o instanceof ClaseIndividual claseIndividual)) return false;
        return Objects.equals(fechaYHora, claseIndividual.fechaYHora);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fechaYHora);
    }
}
