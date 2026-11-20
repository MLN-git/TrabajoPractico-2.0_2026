package Models;

import Exceptions.AlumnosVacioException;
import Interfaces.ICursable;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static Gestores.Menu.consultarAlumno;
import static menu.Verificador.queDiaEs;

public class Comision implements ICursable {
    private String nombreComision;
    private HashSet<Alumno> alumnos;
    private HashMap<LocalDate, HashSet<Alumno>> asistencias;
    private Year anio;

    public Comision(String nombreComision, Year anio) {
        this.nombreComision = nombreComision;
        this.alumnos = new HashSet<>();
        this.anio = anio;
        this.asistencias = new HashMap<>();
    }


    @Override
    public void tomarAsistencia() throws AlumnosVacioException {
        if (alumnos.isEmpty()){
            throw new AlumnosVacioException("No hay alumnos asignados a la clase");
        }
        LocalDate hoy = queDiaEs();

        if (!(this.asistencias.containsKey(hoy))) {
            HashSet<Alumno> asistenciaHoy = new HashSet<>();
            for (Alumno alumno : this.alumnos) {
                if (consultarAlumno(alumno)){
                    asistenciaHoy.add(alumno);
                }
            }
            this.asistencias.put(hoy, asistenciaHoy);
        }
    }

    @Override
    public String listarAsistencias() throws AlumnosVacioException {
        if (asistencias.containsKey(queDiaEs()) || asistencias.get(queDiaEs()).isEmpty()){
            throw new AlumnosVacioException("Nadie asistió todavía");
        }
        StringBuilder s = new StringBuilder();
        for  (Alumno alumno : asistencias.get(queDiaEs())) {
            s.append(alumno.nombreCompleto());
            s.append("\n");
        }
        return s.toString();
    }

    @Override
    public Boolean agregarPresente(Alumno alumno) {
        return asistencias.get(queDiaEs()).add(alumno);
    }

    @Override
    public Boolean eliminarPresente(Alumno alumno) {
        return asistencias.get(queDiaEs()).remove(alumno);
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

    public String getNombreComision() {
        return nombreComision;
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
