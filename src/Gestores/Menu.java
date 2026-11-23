package Gestores;

import Enums.ETipoMateria;
import Exceptions.AlumnosVacioException;
import Exceptions.AsistenciaYaTomadaException;
import Exceptions.ElementoRepetidoException;
import Exceptions.NoSeEncontroElElementoException;
import Interfaces.ICursable;
import Interfaces.IElegible;
import Interfaces.ITieneNombre;
import Models.Alumno;
import Models.ClaseIndividual;
import Models.Comision;
import Models.Materia;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

        int opcion =  leerNumeroPositivoMinMax(1,5);

        switch (opcion){
            case 1:
                crearMateria();
            case 2:
                editarMateria(elegirOpcion(sesion));
            case 3:
                eliminarMateria(elegirOpcion(sesion));
            case 4:
                asistencia(elegirOpcion(elegirOpcion(sesion)));
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
        Materia materia = null;


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

    // Hacer elegibles:
    // Gestora<Materia>, materia<ICursable>, ICursable<Alumno>
    public <T extends ITieneNombre> T elegirOpcion(IElegible<T> elegible){
        // Esta es la version generica
        // EUREKAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        // Estoy escribiendo esto muy feliz porque entendí todo
        ArrayList<T> lista = elegible.getElegibles();

        int numero = 1;
        System.out.println("Seleccione una opcion: \n");

        for (T generiquito : lista){
            System.out.println(numero++ + " - " + generiquito.getNombre());
        }
        // Actualización, volví a entender todo
        // EUREKA 2.0
        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1, lista.size());

        return lista.get(opcion - 1);
    }

    // Version anterior que no usaba bien la genericidad

//    public Materia<?> elegirMateria(){
//        // Listar materias creadas en la gestora
//        ArrayList<Materia<?>> materias = new ArrayList<>(sesion.getMateriasExistentes());
//
//        int numero = 1;
//        System.out.println("Seleccione una materia: \n");
//
//        for (Materia<?> materia : materias){
//            System.out.println(numero++ + " - " + materia.getNombre());
//        }
//        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1, materias.size());
//
//        return materias.get(opcion - 1);
//    }
//
//    public ICursable elegirCursable(Materia<?> materia){
//        ArrayList<ICursable> cursables = new ArrayList<>(materia.getICursables());
//
//        int numero = 1;
//        System.out.println("Seleccione una cursable: \n");
//
//        for (ICursable curso : cursables){
//            System.out.println(numero++ + " - " + materia.getNombre());
//        }
//        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1, cursables.size());
//
//        return  cursables.get(opcion - 1);
//    }
//
//    public Alumno elegirAlumno(ICursable cursable){
//        ArrayList<Alumno> alumnos = new ArrayList<Alumno>(cursable.getAlumnos());
//
//        int numero = 1;
//        System.out.println("Seleccione un alumno: \n");
//
//        for (Alumno alumno : alumnos){
//            System.out.println(numero++ + " - " + alumno.getNombre());
//        }
//        int opcion = menu.Verificador.leerNumeroPositivoMinMax(1, alumnos.size());
//
//        return alumnos.get(opcion - 1);
//    }

    public void eliminarMateria(Materia materia){
        try{
            sesion.darBajaMateria(materia);
        }catch (NoSeEncontroElElementoException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void asistencia(ICursable cursable){
        System.out.println("--MENU ASISTENCIA---\n\n");
        System.out.println("1 - Tomar asistencia\n");
        System.out.println("2 - Listar asistencias\n");
        System.out.println("3 - Agregar asistencia individual\n");
        System.out.println("4 - Eliminar asistencia individual\n");
        System.out.println("5 - Atrás\n");
        //Falta terminar
        switch (menu.Verificador.leerNumeroPositivoMinMax(1,5)){
            case 1:
                try {
                    cursable.tomarAsistencia();
                } catch (AlumnosVacioException e) {
                    System.out.println(e.getMessage());
                }catch (AsistenciaYaTomadaException e){
                    System.out.println(e.getMessage());
                }
            case 2:
                try {
                    cursable.listarAsistencias();
                } catch (AlumnosVacioException e) {
                    throw new RuntimeException(e);
                }
            case 3:
                // Me falta el listar alumnos
                //cursable.agregarPresente(listarAlumnos());
        }
    }

    public void editarMateria(Materia materia){
        System.out.println("---EDITAR MATERIA---\n\n");
        System.out.println("1 - Crear Cursable");
        System.out.println("2 - Borrar Cursable");
        System.out.println("3 - Cambiar Nombre");
        System.out.println("4 - Editar Cursable");
        System.out.println("5 - Atrás");
        int opcion = leerNumeroPositivoMinMax(1,5);
        // TENER EN CUENTA EL ETIPOMATERIA EN CADA OPCION

        switch (opcion){
            case 1:
                switch (materia.getTipoMateria()){
                    case PARTICULAR:

                        try {
                            materia.agregarCursable(crearClaseParticular());
                        } catch (ElementoRepetidoException e) {
                            System.out.println(e.getMessage());
                        }

                    case COMISIONADA:
                        try {
                            materia.agregarCursable(crearComision());
                        }catch (ElementoRepetidoException e) {
                            System.out.println(e.getMessage());
                        }
                }
            case 2:
                try {
                    materia.eliminarCursable(elegirOpcion(materia));
                }catch (NoSeEncontroElElementoException e){
                    System.out.println(e.getMessage());
                }
            case 3:
                materia.setNombre(leerNombre("Escriba el nuevo nombre"));
            case 4:
                menuCursable(elegirOpcion(materia));
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


    public void menuCursable(ICursable curso){
        System.out.println("--EDITAR CURSABLE--");
        System.out.println("1 - ");
        // TERMINAR
        curso.setNombre(menu.Verificador.leerNombre("Escriba el nuevo nombre"));

    }
    public static Boolean consultarAlumno(Alumno alumno) {
        System.out.println(("¿El Alumno: "
                + alumno.getNombre() + " "
                + alumno.getApellido()
                + " Está presente?\n" +
                "1 - Si\n" +
                "2 - No\n"));
        Integer opcion = menu.Verificador.leerNumeroPositivoMinMax(1,2);
        if (opcion == 1) {
            return true;
        }else{
            return false;
        }
    }

}
