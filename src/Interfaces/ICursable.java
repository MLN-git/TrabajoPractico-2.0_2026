package Interfaces;

import Exceptions.*;
import Models.Alumno;

public interface ICursable extends IElegible<Alumno>, ITieneNombre {

    public void agregarAlumno(Alumno alumno) throws ElementoRepetidoException;
    public void eliminarAlumno(Alumno alumno) throws NoSeEncontroElElementoException;
    public String listarAlumnos();
    public void tomarAsistencia() throws AlumnosVacioException, AsistenciaYaTomadaException;
    public String listarAsistencias() throws AlumnosVacioException;
    public void agregarPresente(Alumno alumno) throws NoSeEncontroElElementoException;
    public void eliminarPresente(Alumno alumno) throws SinElementosException, NoSeEncontroElElementoException;
}
