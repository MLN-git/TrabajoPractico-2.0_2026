package Gestores;

import Exceptions.AlumnosVacioException;
import Exceptions.ElementoRepetidoException;
import Exceptions.NoSeEncontroElElementoException;
import Exceptions.SinElementosException;
import Interfaces.IElegible;
import Interfaces.ITieneNombre;
import Models.Alumno;
import Models.Materia;
import Models.Usuario;

import java.util.*;

public class Gestora implements IElegible<Materia> {
    HashMap<UUID, Usuario> usuarios;
    HashMap<UUID, Alumno> alumnosExistentes;
    LinkedHashSet<Materia> materiasExistentes;

    public Gestora() {
        this.usuarios = new HashMap<>();
        this.alumnosExistentes = new HashMap<>();
        this.materiasExistentes = new LinkedHashSet<>();
    }

    public String listarNombresUsuarios() {
        StringBuilder nombres = new StringBuilder();
        for (Usuario usuario : usuarios.values()) {
            nombres.append(usuario.getNombre()).append("\n");
        }
        return nombres.toString();
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

    public boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            return false;
        }
        usuarios.put(usuario.getId(), usuario);
        return true;
    }

    public void agregarAlumno(Alumno alumno) throws ElementoRepetidoException {
        if (alumnosExistentes.containsKey(alumno.getId())) {
            throw new ElementoRepetidoException("El id del Alumno ya existe");
        }
        alumnosExistentes.put(alumno.getId(), alumno);
    }

    public void agregarMateria(Materia materia) throws ElementoRepetidoException {
        if (!(materiasExistentes.add(materia))){
            throw new ElementoRepetidoException("El id de la materia ya está en la cuenta");
        }
    }

    public boolean darBajaUsuario(UUID id) {
        if (!usuarios.containsKey(id)) {
            return false;
        }
        usuarios.remove(id);
        return true;
    }

    public boolean darBajaAlumno(UUID id) {
        if (!alumnosExistentes.containsKey(id)) {
            return false;
        }
        alumnosExistentes.remove(id);
        return true;
    }

    public void darBajaMateria(Materia materia) throws NoSeEncontroElElementoException {
        if (!(materiasExistentes.remove(materia))){
            throw new NoSeEncontroElElementoException("La materia no está en la cuenta");
        };
    }
}
