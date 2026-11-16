package Models;

import Enums.EDiaSemana;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;

public class Comision {
    private Date horaInicio;
    private Date horaFin;
    private EDiaSemana diaSemana;
    private String nombreComision;
    private ArrayList<Alumno> alumnos;
    private Year anio;
}
