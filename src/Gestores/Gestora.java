package Gestores;

import Exceptions.AlumnosVacioException;
import Exceptions.ElementoRepetidoException;
import Exceptions.NoSeEncontroElElementoException;
import Exceptions.SinElementosException;
import Interfaces.ICursable;
import Interfaces.IElegible;
import Models.*;
import Persistence.AlumnoDAO;
import Persistence.ClaseIndividualDAO;
import Persistence.ComisionDAO;
import Persistence.MateriaDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class Gestora implements IElegible<Materia> {
    private final Usuario usuario;
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final MateriaDAO materiaDAO = new MateriaDAO();
    private final ComisionDAO comisionDAO = new ComisionDAO();
    private final ClaseIndividualDAO claseDAO = new ClaseIndividualDAO();
    private HashMap<UUID, Alumno> alumnosExistentes = new HashMap<>();
    private LinkedHashSet<Materia> materiasExistentes= new LinkedHashSet<>();

    public Gestora(Usuario usuario) throws SQLException {
        this.usuario = usuario;
        for (Alumno a : alumnoDAO.listar(usuario.getId())) {
            alumnosExistentes.put(a.getId(), a);
        }
        materiasExistentes.addAll(materiaDAO.listarCompletas(usuario.getId()));
    }



    public String listarAlumnos() {
        StringBuilder alumnos = new StringBuilder();
        for (Alumno alumno : alumnosExistentes.values()) {
            alumnos.append("Nombre: ").append(alumno.getNombre()).append(". Apellido: ").append(alumno.getApellido()).append("\n");
        }
        return alumnos.toString();
    }

    public String listarMaterias() {
        StringBuilder materias = new StringBuilder();
        for (Materia materia : materiasExistentes) {
            materias.append("Nombre: ").append(materia.getNombre()).append("\n");
        }
        return materias.toString();
    }

    // Mini Eureka 3.0
    @Override
    public ArrayList<Materia> getElegibles() throws SinElementosException {
        if (materiasExistentes.isEmpty()) {
            throw new SinElementosException("Todavia no existen materias");
        }
        return new ArrayList<>(materiasExistentes);
    }

    public ArrayList<Alumno> getAlumnosElegibles() throws AlumnosVacioException {
        if  (alumnosExistentes.isEmpty()) {
            throw new AlumnosVacioException("Todavia no hay alumnos");
        }
        return new ArrayList<>(alumnosExistentes.values());
    }

    public void agregarAlumno(Alumno alumno) throws ElementoRepetidoException, SQLException {
        if (alumnosExistentes.containsKey(alumno.getId())) {
            throw new ElementoRepetidoException("El id del Alumno ya existe");
        }
        alumnoDAO.guardarAlumno(alumno, usuario.getId());
        alumnosExistentes.put(alumno.getId(), alumno);
    }

    public void actualizarNombreAlumno(Alumno alumno, String nombre) throws SQLException {
        Alumno actualizado = new Alumno(
                alumno.getId(), nombre, alumno.getApellido(), alumno.getAnioIngreso());
        alumnoDAO.actualizarAlumno(actualizado, usuario.getId());
        alumno.setNombre(nombre);
    }

    public void actualizarApellidoAlumno(Alumno alumno, String apellido) throws SQLException {
        Alumno actualizado = new Alumno(
                alumno.getId(), alumno.getNombre(), apellido, alumno.getAnioIngreso());
        alumnoDAO.actualizarAlumno(actualizado, usuario.getId());
        alumno.setApellido(apellido);
    }

    public void agregarMateria(Materia materia) throws ElementoRepetidoException, SQLException {
        if (materiasExistentes.contains(materia)) {
            throw new ElementoRepetidoException("El id de la materia ya está en la cuenta");
        }
        materiaDAO.guardarMateria(materia, usuario.getId());
        materiasExistentes.add(materia);
    }

//  Ningun alumno deberia ser eliminado, simplemente quedan en la db y se levantan segun el usuario pero no se eliminan
//    public boolean darBajaAlumno(UUID id) {
//        if (!alumnosExistentes.containsKey(id)) {
//            return false;
//        }
//        alumnosExistentes.remove(id);
//        return true;
//    }

    public void darBajaMateria(Materia materia) throws NoSeEncontroElElementoException, SQLException {
        if (!(materiasExistentes.contains(materia))){
            throw new NoSeEncontroElElementoException("La materia no está en la cuenta");
        }
        materiaDAO.eliminarMateria(materia.getId(), usuario.getId());
        materiasExistentes.remove(materia);
    }

    public void actualizarNombreMateria(Materia materia, String nombre) throws SQLException {
        materiaDAO.actualizarNombre(materia.getId(), nombre, usuario.getId());
        materia.setNombre(nombre);
    }

    public void agregarCursable(Materia materia, ICursable cursable)
            throws ElementoRepetidoException, SQLException {
        if (materia.getICursables().contains(cursable)) {
            throw new ElementoRepetidoException("El cursable está repetido");
        }
        guardarCursable(materia, cursable);
        materia.agregarCursable(cursable);
    }

    public void darBajaCursable(Materia materia, ICursable cursable)
            throws NoSeEncontroElElementoException, SQLException {
        if (!materia.getICursables().contains(cursable)) {
            throw new NoSeEncontroElElementoException("El Cursable no ha sido encontrado");
        }
        eliminarCursable(cursable);
        materia.eliminarCursable(cursable);
    }

    private void guardarCursable(Materia materia, ICursable cursable) throws SQLException {
        if (cursable instanceof Comision c) {
            comisionDAO.guardarComision(c, materia.getId(), usuario.getId());
        } else if (cursable instanceof ClaseIndividual ci) {
            claseDAO.guardarClase(ci, materia.getId(), usuario.getId());
        }
    }

    private void eliminarCursable(ICursable cursable) throws SQLException {
        if (cursable instanceof Comision c) {
            comisionDAO.eliminarComision(c.getId(), usuario.getId());
        } else if (cursable instanceof ClaseIndividual ci) {
            claseDAO.eliminarClase(ci.getId(), usuario.getId());
        }
    }

    public void actualizarNombreCursable(ICursable cursable, String nombre)
            throws SQLException {
        if (cursable instanceof Comision c) {
            comisionDAO.actualizarNombre(c.getId(), nombre, usuario.getId());
        } else if (cursable instanceof ClaseIndividual ci) {
            claseDAO.actualizarNombre(ci.getId(), nombre, usuario.getId());
        } else {
            throw new IllegalArgumentException("Tipo de cursable no soportado");
        }
        cursable.setNombre(nombre);
    }

    private void guardarAlumnosDe(ICursable cursable, Collection<Alumno> alumnos)
            throws SQLException {
        if (cursable instanceof Comision c) {
            comisionDAO.guardarAlumnos(c.getId(), alumnos, usuario.getId());
        } else if (cursable instanceof ClaseIndividual ci) {
            claseDAO.guardarAlumnos(ci.getId(), alumnos, usuario.getId());
        }
    }

    public void agregarAlumnoACursable(ICursable cursable, Alumno alumno)
            throws ElementoRepetidoException, SQLException, SinElementosException {
        ArrayList<Alumno> alumnos = cursable.getElegibles();
        if (alumnos.contains(alumno)) {
            throw new ElementoRepetidoException("El alumno ya está en la clase");
        }
        alumnos.add(alumno);
        guardarAlumnosDe(cursable, alumnos);
        cursable.agregarAlumno(alumno);
    }

    public void eliminarAlumnoDeCursable(ICursable cursable, Alumno alumno)
            throws NoSeEncontroElElementoException, SQLException, SinElementosException {
        ArrayList<Alumno> alumnos = cursable.getElegibles();
        if (!alumnos.remove(alumno)) {
            throw new NoSeEncontroElElementoException("El alumno no está en esta clase");
        }
        guardarAlumnosDe(cursable, alumnos);
        cursable.eliminarAlumno(alumno);
    }

    public void guardarAsistencia(ICursable cursable, Collection<Alumno> presentes)
            throws SQLException, SinElementosException {
        if (cursable instanceof Comision c) {
            comisionDAO.guardarAsistencia(
                    c.getId(), LocalDate.now(), c.getElegibles(), presentes, usuario.getId());
            c.cargarAsistencia(LocalDate.now(), new HashSet<>(presentes));
        } else if (cursable instanceof ClaseIndividual ci) {
            LocalDate fecha = ci.getFechaYHora().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            claseDAO.guardarAsistencia(
                    ci.getId(), fecha, ci.getElegibles(), presentes, usuario.getId());
            ci.setAsistencia(new HashSet<>(presentes));
        } else {
            throw new IllegalArgumentException("Tipo de cursable no soportado");
        }
    }

    public void agregarPresente(ICursable cursable, Alumno alumno)
            throws NoSeEncontroElElementoException, SQLException, SinElementosException {
        if (!cursable.getElegibles().contains(alumno)) {
            throw new NoSeEncontroElElementoException("Alumno no encontrado en la cursable");
        }
        HashSet<Alumno> presentes = presentesDeHoy(cursable);
        presentes.add(alumno);
        guardarAsistencia(cursable, presentes);
    }

    public void eliminarPresente(ICursable cursable, Alumno alumno)
            throws NoSeEncontroElElementoException, SinElementosException, SQLException {
        HashSet<Alumno> presentes = presentesDeHoy(cursable);
        if (presentes.isEmpty() && !asistenciaTomada(cursable)) {
            throw new SinElementosException("Todavia no se tomó asistencia");
        }
        if (!presentes.remove(alumno)) {
            throw new NoSeEncontroElElementoException("El alumno no ha asistido");
        }
        guardarAsistencia(cursable, presentes);
    }

    private HashSet<Alumno> presentesDeHoy(ICursable cursable) {
        if (cursable instanceof Comision c) {
            HashSet<Alumno> presentes = c.getAsistencias().get(LocalDate.now());
            return presentes == null ? new HashSet<>() : new HashSet<>(presentes);
        }
        if (cursable instanceof ClaseIndividual ci) {
            return new HashSet<>(ci.getAsistencia());
        }
        throw new IllegalArgumentException("Tipo de cursable no soportado");
    }

    private boolean asistenciaTomada(ICursable cursable) {
        if (cursable instanceof Comision c) {
            return c.getAsistencias().containsKey(LocalDate.now());
        }
        if (cursable instanceof ClaseIndividual ci) {
            return ci.isAsistenciaTomada();
        }
        throw new IllegalArgumentException("Tipo de cursable no soportado");
    }
}
