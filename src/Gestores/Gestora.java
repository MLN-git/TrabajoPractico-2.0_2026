package Gestores;

import Exceptions.AlumnosVacioException;
import Exceptions.ElementoRepetidoException;
import Exceptions.NoSeEncontroElElementoException;
import Exceptions.SinElementosException;
import Interfaces.IElegible;
import Models.Alumno;
import Models.Materia;
import Models.Usuario;
import Persistence.AlumnoDAO;
import Persistence.MateriaDAO;

import java.sql.SQLException;
import java.util.*;

public class Gestora implements IElegible<Materia> {
    private final Usuario usuario;
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final MateriaDAO materiaDAO = new MateriaDAO();
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

    public void agregarMateria(Materia materia) throws ElementoRepetidoException {
        if (!(materiasExistentes.add(materia))){
            throw new ElementoRepetidoException("El id de la materia ya está en la cuenta");
        }
        materiaDAO.guardarMateria(materia, usuario.getId());
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
}
