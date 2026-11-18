package Models;

import Enums.EDiaSemana;
import Exceptions.AlumnosVacioException;
import Interfaces.ICursable;

import java.time.Year;
import java.util.*;

import static Gestores.Menu.consultarAlumno;

public class Comision implements ICursable {
    private EDiaSemana diaSemana;
    private String nombreComision;
    private HashSet<Alumno> alumnos;
    private HashMap<Date, HashSet<Alumno>> asistencias;
    private Year anio;

    public Comision(EDiaSemana diaSemana, String nombreComision, HashSet<Alumno> alumnos, Year anio) {
        this.diaSemana = diaSemana;
        this.nombreComision = nombreComision;
        this.alumnos = alumnos;
        this.anio = anio;
    }


    @Override
    public void tomarAsistencia() throws AlumnosVacioException {
        if (alumnos.isEmpty()){
            throw new AlumnosVacioException("No hay alumnos asignados a la clase");
        }
        if (this.asistencias == null) {
            this.asistencias = new HashSet<>();
            for (Alumno alumno : this.alumnos) {
                if (consultarAlumno(alumno)){
                    this.asistencias.add(alumno);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comision comision)) return false;
        return Objects.equals(nombreComision, comision.nombreComision);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombreComision);
    }

    public EDiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(EDiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getNombreComision() {
        return nombreComision;
    }

    public void setNombreComision(String nombreComision) {
        this.nombreComision = nombreComision;
    }

    public HashSet<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(HashSet<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public Year getAnio() {
        return anio;
    }

    public void setAnio(Year anio) {
        this.anio = anio;
    }
}
