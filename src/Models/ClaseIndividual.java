package Models;

import Exceptions.AlumnosVacioException;
import Interfaces.ICursable;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static Gestores.Menu.consultarAlumno;

public class ClaseIndividual implements ICursable {
    private UUID id;
    private Date fechaYHora;
    private String nombre;
    private HashSet<Alumno> alumnos;
    private HashSet<Alumno> asistencia;
    private boolean asistenciaTomada;

    public ClaseIndividual(Date fechaYHora, String nombre) {
        this(UUID.randomUUID(), fechaYHora, nombre);
    }

    public ClaseIndividual(UUID id, Date fechaYHora, String nombre) {
        this.id = id;
        this.fechaYHora = fechaYHora;
        this.nombre = nombre;
        this.alumnos = new HashSet<>();
        this.asistencia = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public HashSet<Alumno> getAlumnos() {
        return alumnos;
    }

    public HashSet<Alumno> getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(HashSet<Alumno> asistencia) {
        this.asistencia = asistencia;
        this.asistenciaTomada = true;
    }

    public boolean isAsistenciaTomada() {
        return asistenciaTomada;
    }

    public void marcarAsistenciaTomada() {
        this.asistenciaTomada = true;
    }

    @Override
    public void tomarAsistencia() throws AlumnosVacioException {
        if (alumnos.isEmpty()){
            throw new AlumnosVacioException("No hay alumnos asignados a la clase");
        }
        this.asistencia.clear();
        for (Alumno alumno : this.alumnos) {
            if (consultarAlumno(alumno)) {
                this.asistencia.add(alumno);
            }
        }
        this.asistenciaTomada = true;
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
        if (!alumnos.contains(alumno)) {
            return false;
        }
        asistenciaTomada = true;
        return asistencia.add(alumno);
    }

    @Override
    public Boolean eliminarPresente(Alumno alumno) {
        if (asistencia == null) {
            return false;
        }
        return asistencia.remove(alumno);
    }

    @Override
    public Boolean agregarAlumno(Alumno alumno) {
        return alumnos.add(alumno);
    }

    @Override
    public Boolean eliminarAlumno(Alumno alumno) {
        boolean eliminado = alumnos.remove(alumno);
        if (eliminado) {
            asistencia.remove(alumno);
        }
        return eliminado;
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
        return Objects.equals(id, claseIndividual.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
