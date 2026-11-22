package Gestores;

import Enums.ETipoMateria;
import Exceptions.AlumnosVacioException;
import Exceptions.ElementoRepetidoException;
import Exceptions.NoSeEncontroElElementoException;
import Interfaces.ICursable;
import Models.Alumno;
import Models.ClaseIndividual;
import Models.Comision;
import Models.Materia;

import java.lang.reflect.Array;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

import static menu.Verificador.*;

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
                editarMateria(listarMaterias());
            case 3:
                eliminarMateria(listarMaterias());
            case 4:
                asistencia(listarMaterias());
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

    public Materia<?> listarMaterias(){
        // Listar materias creadas en la gestora
        ArrayList<Materia<?>> materias = new ArrayList<>(sesion.getMateriasExistentes());

        int numero = 1;
        System.out.println("Seleccione una materia: \n");

        for (Materia<?> materia : materias){
            System.out.println(numero++ + " - " + materia.getNombre());
        }
        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1, materias.size());

        return materias.get(opcion - 1);
    }

    public ICursable listarCursables(Materia<?> materia){
        ArrayList<ICursable> cursables = new ArrayList<>(materia.getICursables());

        int numero = 1;
        System.out.println("Seleccione una cursable: \n");

        for (ICursable curso : cursables){
            System.out.println(numero++ + " - " + materia.getNombre());
        }
        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1, cursables.size());

        return  cursables.get(opcion - 1);
    }

    public void eliminarMateria(Materia<?> materia){
        try{
            sesion.darBajaMateria(materia);
        }catch (NoSeEncontroElElementoException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void asistencia(Materia<?> materia){
        ICursable cursable = listarCursables(materia);

        try {
            cursable.tomarAsistencia();
        } catch (AlumnosVacioException e) {
            System.out.println(e.getMessage());
        }
    }

    public void editarMateria(Materia<?> materia){
        System.out.println("---EDITAR MATERIA---\n\n");
        System.out.println("1 - Crear Cursable");
        System.out.println("2 - Borrar Cursable");
        System.out.println("3 - Cambiar Nombre");
        System.out.println("4 - Editar Cursable");
        System.out.println("5 - Atrás");
        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1,5);
        // TENER EN CUENTA EL ETIPOMATERIA EN CADA OPCION

        switch (opcion){
            case 1:
                switch (materia.getTipoMateria()){
                    case PARTICULAR:
                        @SuppressWarnings("unchecked")
                        Materia<ClaseIndividual> particular =
                                (Materia<ClaseIndividual>) materia;
                        particular.agregarCursable(crearClaseParticular());
                        break;
                    case COMISIONADA:
                        @SuppressWarnings("unchecked")
                        Materia<Comision> comisiona = (Materia<Comision>) materia;
                        comisiona.agregarCursable(crearComision());
                        break;
                }
            case 2:

            case 3:
            case 4:
            case 5:
                break;
        }

    }

    public Comision crearComision(){

        String nombre = menu.Verificador.leerNombre("Ingrese el nombre de la comision: ");
        int anio = menu.Verificador.leerNumeroPositivoMinMax(2026,2050);

        return new Comision(nombre, anio);

    }

    public ClaseIndividual crearClaseParticular(){
        System.out.println("¿A que hora se cursa esta clase? (solo hora en punto de 1 a 24)");
        System.out.println("Ten en cuenta que la hora no se puede cambiar");
        int hora = menu.Verificador.leerNumeroPositivoMinMax(1,24);

        System.out.println("Ingrese el mes(1/12): ");
        int mes = menu.Verificador.leerNumeroPositivoMinMax(1,12);
        int dia = menu.Verificador.pedirDiaSegunMes(mes);

        String nombre = menu.Verificador.leerNombre("Ingrese el nombre del materia: ");

        LocalDateTime fechayHora = LocalDateTime.of(queAnioEs(),mes, dia, hora, 0);
        Date fecha = Date.from(fechayHora.atZone(ZoneId.systemDefault()).toInstant());
        return new ClaseIndividual(fecha, nombre);

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
