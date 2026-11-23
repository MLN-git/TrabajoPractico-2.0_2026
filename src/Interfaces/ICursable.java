package Interfaces;

import Exceptions.AlumnosVacioException;
import Exceptions.AsistenciaYaTomadaException;
import Exceptions.NoSeEncontroElElementoException;
import Exceptions.SinElementosException;
import Models.Alumno;
import Models.Materia;

import java.util.HashSet;

public interface ICursable extends IElegible<Alumno>, ITieneNombre {

    public Boolean agregarAlumno(Alumno alumno);
    public Boolean eliminarAlumno(Alumno alumno);
    public String listarAlumnos();
    public void tomarAsistencia() throws AlumnosVacioException, AsistenciaYaTomadaException;
    public String listarAsistencias() throws AlumnosVacioException;
    public void agregarPresente(Alumno alumno) throws NoSeEncontroElElementoException;
    public void eliminarPresente(Alumno alumno) throws SinElementosException, NoSeEncontroElElementoException;
    public void setNombre(String nombre);
}
