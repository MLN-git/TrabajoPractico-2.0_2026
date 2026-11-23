package Gestores;

import Enums.ETipoMateria;
import Exceptions.*;
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

    // Terminado
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
                    break;
                case 2:
                    // Ingresar a Alumnos
                    menuAlumnos();
                    break;
                case 3:
                    System.out.println("\nSaliendo...");
                    scanner.close();
                    break;

                default:
                    System.out.println("\nOpción no válida.");
            }
        }  while (opcionMenu != 3);
    }

    // Terminado
    public void menuMaterias(){
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
                break;
            case 2:
                try {
                    menuEditarMateria(extraerElegibles(sesion));
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                try {
                    eliminarMateria(extraerElegibles(sesion));
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                try {
                    menuAsistencia(extraerElegibles(extraerElegibles(sesion)));
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 5:
                break;
        }
    }

    // Terminado
    public void menuAlumnos(){
        // Agregar opciones del menu alumnos
        System.out.println("---ALUMNOS---\n\n");
        System.out.println("1 - Crear Alumno\n");
        System.out.println("2 - Cambiar nombre\n");
        System.out.println("3 - Cambiar apellido\n");
        System.out.println("4 - Atrás");

        switch (leerNumeroPositivoMinMax(1,4)){
            case 1:
                crearAlumno();
                break;
            case 2:
                try {
                    elegirOpcion(sesion.getAlumnosElegibles()).setNombre(
                                    menu.Verificador.leerNombre(
                                            "Escriba el primer nombre del alumno sin espacios: "));
                } catch (AlumnosVacioException e) {
                    throw new RuntimeException(e);
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());

                }
                break;
            case 3:
                try {
                    try {
                        elegirOpcion(sesion.getAlumnosElegibles()).setApellido(
                                menu.Verificador.leerNombre(
                                        "Escriba el primer apellido del alumno sin espacios: "));
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());

                    }
                } catch (AlumnosVacioException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 4:
                break;
        }


    }

    // Terminado
    public void menuAsistencia(ICursable cursable){
        System.out.println("--MENU ASISTENCIA---\n\n");
        System.out.println("1 - Tomar asistencia\n");
        System.out.println("2 - Listar asistencias\n");
        System.out.println("3 - Agregar asistencia individual\n");
        System.out.println("4 - Eliminar asistencia individual\n");
        System.out.println("5 - Atrás\n");


        switch (menu.Verificador.leerNumeroPositivoMinMax(1,5)){
            case 1:
                try {
                    cursable.tomarAsistencia();
                } catch (AlumnosVacioException e) {
                    System.out.println(e.getMessage());
                } catch (AsistenciaYaTomadaException e){
                    System.out.println(e.getMessage());
                }
                break;
            case 2:
                try {
                    System.out.println(cursable.listarAsistencias());
                } catch (AlumnosVacioException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                try {
                    cursable.agregarPresente(elegirOpcion(cursable.getElegibles()));
                } catch (NoSeEncontroElElementoException e) {
                    System.out.println(e.getMessage());
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 4:
                try {
                    cursable.eliminarPresente(elegirOpcion(cursable.getElegibles()));
                } catch (NoSeEncontroElElementoException e) {
                    System.out.println(e.getMessage());
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 5:
                break;
        }
    }

    // Terminado
    public void menuEditarMateria(Materia materia){
        System.out.println("---EDITAR MATERIA---\n\n");
        System.out.println("1 - Crear Cursable");
        System.out.println("2 - Borrar Cursable");
        System.out.println("3 - Cambiar Nombre");
        System.out.println("4 - Editar Cursable");
        System.out.println("5 - Atrás");
        int opcion = leerNumeroPositivoMinMax(1,5);

        switch (opcion){
            case 1:
                switch (materia.getTipoMateria()){
                    case PARTICULAR:

                        try {
                            materia.agregarCursable(crearClaseParticular());
                        } catch (ElementoRepetidoException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case COMISIONADA:
                        try {
                            materia.agregarCursable(crearComision());
                        }catch (ElementoRepetidoException e) {
                            System.out.println(e.getMessage());
                        }
                }
                break;
            case 2:
                try {
                    materia.eliminarCursable(extraerElegibles(materia));
                }catch (NoSeEncontroElElementoException e){
                    System.out.println(e.getMessage());
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                materia.setNombre(leerNombre("Escriba el nuevo nombre"));
                break;
            case 4:
                try {
                    menuCursable(extraerElegibles(materia));
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 5:
                break;
        }

    }

    // Terminado
    public void menuCursable(ICursable curso) {
        System.out.println("--EDITAR CURSABLE--");
        System.out.println("1 - Agregar Alumno");
        System.out.println("2 - Eliminar Alumno");
        System.out.println("3 - Cambiar Nombre");
        System.out.println("4 - Atrás");
        int opcion = leerNumeroPositivoMinMax(1, 4);

        switch (opcion) {
            case 1:
                try {
                    // Esta línea de codigo es ARTE RENACENTISTA
                    curso.agregarAlumno(elegirOpcion(sesion.getAlumnosElegibles()));
                } catch (ElementoRepetidoException e) {
                    System.out.println(e.getMessage());
                } catch (AlumnosVacioException e) {
                    System.out.println(e.getMessage());
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());

                }
                break;
            case 2:
                try {
                    curso.eliminarAlumno(elegirOpcion(sesion.getAlumnosElegibles()));
                }catch (NoSeEncontroElElementoException e){
                    System.out.println(e.getMessage());
                }catch (AlumnosVacioException e) {
                    System.out.println(e.getMessage());
                } catch (SinElementosException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 3:
                curso.setNombre(menu.Verificador.leerNombre("Escriba el nuevo nombre"));
                break;
            case 4:
                break;
        }
//        curso.setNombre(menu.Verificador.leerNombre("Escriba el nuevo nombre"));

    }

    // Eureka 4.0
    // ahora acabo de dividir la funcion elegirOpcion para que no tenga que
    // si o si pedir un IElegible y que pida un arraylist así puedo usar
    // el getAlumnosElegibles así como hago en el menuCursable
    public <T extends ITieneNombre> T extraerElegibles(IElegible<T> elegible) throws SinElementosException {

            ArrayList<T> lista;
            lista = elegible.getElegibles();
            return elegirOpcion(lista);
    }

    // Hice elegibles:
    // Gestora<Materia>, materia<ICursable>, ICursable<Alumno>
    public <T extends ITieneNombre> T elegirOpcion(ArrayList<T> lista) throws SinElementosException {
        // Esta es la version generica
        // EUREKAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        // Estoy escribiendo esto muy feliz porque entendí todo
        if (lista.isEmpty()){
            throw new SinElementosException("La lista está vacia");
        }
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

//     Version anterior que no usaba bien la genericidad

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
                break;
            case 2:
                // Materia Particular
                materia = new Materia(nombre, ETipoMateria.PARTICULAR);
                break;
            default:
                System.out.println("\nOpción no válida.");
        }

        if (!(materia == null)){
            try {
                sesion.agregarMateria(materia);
            } catch (ElementoRepetidoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void eliminarMateria(Materia materia){
        try{
            sesion.darBajaMateria(materia);
        }catch (NoSeEncontroElElementoException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
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

        try {
            sesion.agregarAlumno(alumno);
        } catch (ElementoRepetidoException e) {
            System.out.println(e.getMessage());
        }
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
