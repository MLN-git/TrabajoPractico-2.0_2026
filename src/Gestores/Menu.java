package Gestores;

import Models.Alumno;

import static menu.Verificador.leerBoolean;

public class Menu {

    public static Boolean consultarAlumno(Alumno alumno) {
        return leerBoolean("¿El Alumno: "
                                    + alumno.getNombre() + " "
                                    + alumno.getApellido()
                                    + "\nEstá presente? (true/false)");
    }

}
