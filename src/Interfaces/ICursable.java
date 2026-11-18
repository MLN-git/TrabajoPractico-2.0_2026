package Interfaces;

import Exceptions.AlumnosVacioException;
import Models.Alumno;

public interface ICursable {

    public Boolean agregarAlumno(Alumno alumno);
    public Boolean eliminarAlumno(Alumno alumno);
    public String listarAlumnos();
    public void tomarAsistencia() throws AlumnosVacioException;
    public String listarAsistencias() throws AlumnosVacioException;
    public Boolean agregarPresente(Alumno alumno);
    public Boolean eliminarPresente(Alumno alumno);

}
