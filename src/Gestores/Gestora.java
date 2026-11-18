package Gestores;

import Models.Alumno;
import Models.Materia;
import Models.Usuario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Gestora {
    HashMap<UUID, Usuario> usuarios;
    HashMap<UUID, Alumno> alumnosExistentes;
    HashSet<Materia> materiasExistentes;

    public Gestora() {
        this.usuarios = new HashMap<>();
        this.alumnosExistentes = new HashMap<>();
        this.materiasExistentes = new HashSet<>();
    }

    private String listarNombresUsuarios() {
        StringBuilder nombres = new StringBuilder();
        for (Usuario usuario : usuarios.values()) {
            nombres.append(usuario.getNombre()).append("\n");
        }
        return nombres.toString();
    }

    private String listarAlumnos() {
        StringBuilder alumnos = new StringBuilder();
        for (Alumno alumno : alumnosExistentes.values()) {
            alumnos.append("Nombre: ").append(alumno.getNombre()).append(". Apellido: ").append(alumno.getApellido()).append("\n");
        }
        return alumnos.toString();
    }

    private String listarMaterias() {
        StringBuilder materias = new StringBuilder();
        for (Materia materia : materiasExistentes) {
            materias.append("Nombre: ").append(materia.getNombre()).append("\n");
        }
        return materias.toString();
    }

    private boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            return false;
        }
        usuarios.put(usuario.getId(), usuario);
        return true;
    }

    private boolean agregarAlumno(Alumno alumno) {
        if (alumnosExistentes.containsKey(alumno.getId())) {
            return false;
        }
        alumnosExistentes.put(alumno.getId(), alumno);
        return true;
    }

    private boolean agregarMateria(Materia materia) {
        if (materiasExistentes.contains(materia)) {
            return false;
        }
        materiasExistentes.add(materia);
        return true;
    }

    private boolean darBajaUsuario(UUID id) {
        if (!usuarios.containsKey(id)) {
            return false;
        }
        usuarios.remove(id);
        return true;
    }

    private boolean darBajaAlumno(UUID id) {
        if (!alumnosExistentes.containsKey(id)) {
            return false;
        }
        alumnosExistentes.remove(id);
        return true;
    }

    private boolean darBajaMateria(Materia materia) {
        if (!materiasExistentes.contains(materia)) {
            return false;
        }
        materiasExistentes.remove(materia);
        return true;
    }
}
