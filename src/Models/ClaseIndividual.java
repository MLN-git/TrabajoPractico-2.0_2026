package Models;

import Exceptions.*;
import Interfaces.ICursable;

import java.util.*;

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
        this.asistenciaTomada = false;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public ArrayList<Alumno> getElegibles() {
        return new ArrayList<Alumno>(alumnos);
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
    public void tomarAsistencia() throws AlumnosVacioException, AsistenciaYaTomadaException {
        if (alumnos.isEmpty()){
            throw new AlumnosVacioException("No hay alumnos asignados a la clase");
        } else if (asistenciaTomada) {
            throw new AsistenciaYaTomadaException("La asistencia de esta clase ya está tomada");
        }
        for (Alumno alumno : this.alumnos) {
            if (consultarAlumno(alumno)) {
                this.asistencia.add(alumno);
            }
        }
        marcarAsistenciaTomada();
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
    public void agregarPresente(Alumno alumno) throws NoSeEncontroElElementoException {
        if (!alumnos.contains(alumno)) {
            throw new NoSeEncontroElElementoException("Alumno no encontrado en la cursable");
        }
         asistencia.add(alumno);
    }

    @Override
    public void eliminarPresente(Alumno alumno) throws SinElementosException, NoSeEncontroElElementoException {
        if (asistencia == null) {
            throw new SinElementosException("Todavia no se tomó asistencia");
        } else if (!asistencia.contains(alumno)) {
            throw new NoSeEncontroElElementoException("El alumno no ha asistido");
        }
        asistencia.remove(alumno);
    }

    @Override
    public void agregarAlumno(Alumno alumno) throws ElementoRepetidoException {
        if (!(alumnos.add(alumno))){
            throw new ElementoRepetidoException("El alumno ya está en la clase");
        }
    }

    @Override
    public void eliminarAlumno(Alumno alumno) throws NoSeEncontroElElementoException {
        if (!(alumnos.remove(alumno))) {
            throw new NoSeEncontroElElementoException("El alumno no está en esta clase");
        }
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
