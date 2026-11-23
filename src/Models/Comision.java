package Models;

import Exceptions.AlumnosVacioException;
import Exceptions.AsistenciaYaTomadaException;
import Exceptions.NoSeEncontroElElementoException;
import Exceptions.SinElementosException;
import Interfaces.ICursable;

import java.time.LocalDate;
import java.util.*;

import static Gestores.Menu.consultarAlumno;
import static menu.Verificador.queDiaEs;

public class Comision implements ICursable{
    private UUID id;
    private String nombreComision;
    private HashSet<Alumno> alumnos;
    private HashMap<LocalDate, HashSet<Alumno>> asistencias;
    private Integer anio;

    public Comision(String nombreComision, Integer anio) {
        this(UUID.randomUUID(), nombreComision, anio);
    }

    public Comision(UUID id, String nombreComision, Integer anio) {
        this.id = id;
        this.nombreComision = nombreComision;
        this.alumnos = new HashSet<>();
        this.anio = anio;
        this.asistencias = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public HashMap<LocalDate, HashSet<Alumno>> getAsistencias() {
        return asistencias;
    }

    public void cargarAsistencia(LocalDate fecha, HashSet<Alumno> presentes) {
        asistencias.put(fecha, presentes);
    }

    @Override
    public void tomarAsistencia() throws AlumnosVacioException, AsistenciaYaTomadaException {
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
        }else{
            throw new AsistenciaYaTomadaException("La asistencia ya ha sido tomada hoy");
        }
    }

    @Override
    public String listarAsistencias() throws AlumnosVacioException {
        HashSet<Alumno> asistenciaHoy = asistencias.get(queDiaEs());
        if (asistenciaHoy == null || asistenciaHoy.isEmpty()) {
            throw new AlumnosVacioException("Nadie asistió todavía");
        }
        StringBuilder s = new StringBuilder();
        for (Alumno alumno : asistenciaHoy) {
            s.append(alumno.nombreCompleto());
            s.append("\n");
        }
        return s.toString();
    }

    @Override
    public void agregarPresente(Alumno alumno) throws NoSeEncontroElElementoException {
        if (!alumnos.contains(alumno)) {
            throw new NoSeEncontroElElementoException("Alumno no encontrado en la comision");
        }
         asistencias.computeIfAbsent(queDiaEs(), dia -> new HashSet<>()).add(alumno);
    }

    @Override
    public void eliminarPresente(Alumno alumno) throws NoSeEncontroElElementoException, SinElementosException {
        HashSet<Alumno> asistenciaHoy = asistencias.get(queDiaEs());
        if (asistenciaHoy == null) {
            throw new SinElementosException("Todavia no se tomó asistencia");
        } else if (!asistenciaHoy.contains(alumno)) {
            throw new NoSeEncontroElElementoException("El alumno no ha asistido");
        }
        asistenciaHoy.remove(alumno);
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
        return Objects.equals(id, comision.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getNombre() {
        return nombreComision;
    }

    public void setNombre(String nombre) {
        this.nombreComision = nombre;
    }

    @Override
    public ArrayList<Alumno> getElegibles() {
        return new ArrayList<Alumno>(alumnos);
    }

    public void setAlumnos(HashSet<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
}
