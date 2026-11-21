package Gestores;

import Enums.ETipoMateria;
import Models.Alumno;
import Models.Materia;

import java.time.Year;
import java.util.Scanner;

import static menu.Verificador.leerBoolean;
import static menu.Verificador.queDiaEs;

public class Menu {
    private Gestora sesion;
    protected Scanner scanner = new Scanner(System.in);
    Integer opcion;


    public Menu(Gestora sesion) {
        this.sesion = sesion;
    }

    public void mostrarMenu(){
        System.out.println("---MENU---\n\n");
        System.out.println("1 - Materias\n");
        System.out.println("2 - Alumnos\n");
        System.out.println("3 - Salir\n");
        opcion = menu.Verificador.leerNumeroPositivoMinMax(1,3);

        switch (opcion){
            case 1:
                // Ingresar a materias
            case 2:
                // Ingresar a Alumnos
            case 3:
                System.out.println("\nSaliendo...");
                scanner.close();
                break;

            default:
                System.out.println("\nOpción no válida.");
        }
    }

    public void menuMaterias(){
        // Agregar opciones del menu materias
    }

    public void menuAlumnos(){
        // Agregar opciones del menu alumnos
    }

    public void crearMateria(){
        System.out.println("1 - Materia comisionada\n");
        System.out.println("2 - Materia de Clases Particulares\n");
        opcion = menu.Verificador.leerNumeroPositivoMinMax(1,2);

        String nombre = menu.Verificador.leerNombre("Ingrese el nombre del materia: ");
        Materia materia;


        switch (opcion){
            case 1:
                // Materia Comisionada
                materia = new Materia(nombre, ETipoMateria.COMISIONADA);
            case 2:
                // Materia Particular
                materia = new Materia(nombre, ETipoMateria.PARTICULAR);
            default:
                System.out.println("\nOpción no válida.");
        }

        // Meter Try catch para ingresar materia a la gestora
    }

    public void editarMateria(){
        // Listar materias creadas en la gestora

    }

    public void crearAlumno(){
        Alumno alumno = new Alumno(
                menu.Verificador.leerNombre("Escriba solo el nombre del alumno: "),
                menu.Verificador.leerNombre("Escriba solo el apellido del alumno: "),
                queDiaEs().getYear()
        );

        sesion.agregarAlumno(alumno);
    }


    public static Boolean consultarAlumno(Alumno alumno) {
        return leerBoolean("¿El Alumno: "
                                    + alumno.getNombre() + " "
                                    + alumno.getApellido()
                                    + "\nEstá presente? (true/false)");
    }

}
