package Gestores;

import Enums.ETipoMateria;
import Exceptions.ElementoRepetidoException;
import Models.Alumno;
import Models.Materia;

import java.time.Year;
import java.util.LinkedHashSet;
import java.util.Scanner;

import static menu.Verificador.leerBoolean;
import static menu.Verificador.queDiaEs;

public class Menu {
    private Gestora sesion;
    protected Scanner scanner = new Scanner(System.in);
    Integer opcionMenu;


    public Menu(Gestora sesion) {
        this.sesion = sesion;
    }

    public void mostrarMenu(){
        do {
            System.out.println("---MENU---\n\n");
            System.out.println("1 - Materias\n");
            System.out.println("2 - Alumnos\n");
            System.out.println("3 - Salir\n");
            opcionMenu = menu.Verificador.leerNumeroPositivoMinMax(1,3);

            switch (opcionMenu){
                case 1:
                    // Ingresar a materias
                    menuMaterias();
                case 2:
                    // Ingresar a Alumnos
                    menuAlumnos();
                case 3:
                    System.out.println("\nSaliendo...");
                    scanner.close();
                    break;

                default:
                    System.out.println("\nOpción no válida.");
            }
        }  while (opcionMenu != 3);
    }

    public void menuMaterias(){
        // Agregar opciones del menu materias
        System.out.println("---MATERIAS---\n\n");
        System.out.println("1 - Crear Materia\n");
        System.out.println("2 - Editar Materia\n");
        System.out.println("3 - Eliminar Materia\n");
        System.out.println("4 - Asistencia\n");
        System.out.println("5 - Atrás\n");

        int opcion =  menu.Verificador.leerNumeroPositivoMinMax(1,5);

        switch (opcion){
            case 1:
                crearMateria();
            case 2:
            case 3:
            case 4:
            case 5:
                break;
        }
    }

    public void menuAlumnos(){
        // Agregar opciones del menu alumnos
        System.out.println("---ALUMNOS---\n\n");
        System.out.println("1 - Crear Alumno\n");
        System.out.println("2 - Cambiar nombre\n");
        System.out.println("3 - Cambiar apellido\n");


    }

    public void crearMateria(){
        System.out.println("1 - Materia comisionada\n");
        System.out.println("2 - Materia de Clases Particulares\n");
        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1,2);

        String nombre = menu.Verificador.leerNombre("Ingrese el nombre del materia: ");
        Materia<?> materia = null;


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

        if (!(materia == null)){
            sesion.agregarMateria(materia);
        }
    }

    public void editarMateria(){
        // Listar materias creadas en la gestora
        LinkedHashSet<Materia<?>> materias = sesion.getMateriasExistentes();

        int numero = 1;
        System.out.println("Seleccione una materia: \n");

        for (Materia<?> materia : materias){
            System.out.println(numero++ + " - " + materia.getNombre());
        }

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
