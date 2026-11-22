package Gestores;

import Exceptions.NoSeEncontroElElementoException;
import Models.Alumno;
import Models.Materia;
import Models.Usuario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class Gestora {
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

    public LinkedHashSet<Materia<?>> getMateriasExistentes() {
        return new LinkedHashSet<>(materiasExistentes);
    }

    public boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            return false;
        }
        usuarios.put(usuario.getId(), usuario);
        return true;
    }

    public boolean agregarAlumno(Alumno alumno) {
        if (alumnosExistentes.containsKey(alumno.getId())) {
            return false;
        }
        alumnosExistentes.put(alumno.getId(), alumno);
        return true;
    }

    public boolean agregarMateria(Materia<?> materia) {
        return materiasExistentes.add(materia);
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

    public void darBajaMateria(Materia<?> materia) throws NoSeEncontroElElementoException {
        if (!(materiasExistentes.remove(materia))){
            throw new NoSeEncontroElElementoException("La materia no está en la cuenta");
        };
    }
}
