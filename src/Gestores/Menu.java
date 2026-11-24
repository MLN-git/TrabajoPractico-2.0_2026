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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
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
            System.out.println("---MENU---\n");
            System.out.println("1 - Materias");
            System.out.println("2 - Alumnos");
            System.out.println("3 - Salir");
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
        int opcion;
        do{
            System.out.println("---MATERIAS---\n");
            System.out.println("1 - Crear Materia");
            System.out.println("2 - Editar Materia");
            System.out.println("3 - Eliminar Materia");
            System.out.println("4 - Asistencia");
            System.out.println("5 - Atrás");

            opcion =  leerNumeroPositivoMinMax(1,5);

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
        }  while (opcion != 5);
    }

    // Terminado
    public void menuAlumnos(){
        int opcion;
        do {
            System.out.println("---ALUMNOS---\n");
            System.out.println("1 - Crear Alumno");
            System.out.println("2 - Cambiar nombre");
            System.out.println("3 - Cambiar apellido");
            System.out.println("4 - Atrás");
            opcion = leerNumeroPositivoMinMax(1, 4);

            switch (opcion) {
                case 1:
                    crearAlumno();
                    break;
                case 2:
                    try {
                        Alumno alumno = elegirOpcion(sesion.getAlumnosElegibles());
                        String nombre = menu.Verificador.leerNombre(
                                "Escriba el primer nombre del alumno sin espacios: ");
                        sesion.actualizarNombreAlumno(alumno, nombre);
                    } catch (SQLException e) {
                        System.out.println("No se pudo actualizar el alumno en la base de datos");
                    } catch (AlumnosVacioException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());

                    }
                    break;
                case 3:
                    try {
                        Alumno alumno = elegirOpcion(sesion.getAlumnosElegibles());
                        String apellido = menu.Verificador.leerNombre(
                                "Escriba el primer apellido del alumno sin espacios: ");
                        sesion.actualizarApellidoAlumno(alumno, apellido);
                    } catch (AlumnosVacioException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("No se pudo actualizar el alumno en la base de datos");
                    }
                    break;
                case 4:
                    break;
            }
        } while (opcion != 4);


    }

    // Terminado
    public void menuAsistencia(ICursable cursable){
        int opcion;
        do {
            System.out.println("--MENU ASISTENCIA---\n");
            System.out.println("1 - Tomar asistencia");
            System.out.println("2 - Listar asistencias");
            System.out.println("3 - Agregar asistencia individual");
            System.out.println("4 - Eliminar asistencia individual");
            System.out.println("5 - Atrás");

            opcion = menu.Verificador.leerNumeroPositivoMinMax(1,5);
            switch (opcion) {
                case 1:
                    tomarAsistenciaPersistida(cursable);
                    break;
                case 2:
                    try {
                        if (cursable instanceof Comision comision){
                            System.out.println("Ingrese el mes a consultar: ");
                            int mes = menu.Verificador.leerNumeroPositivoMinMax(1,12);
                            int dia = menu.Verificador.pedirDiaSegunMes(mes, comision.getAnio());
                            System.out.println(comision.listarAsistenciasxDia(LocalDate.of(comision.getAnio(), mes, dia)));
                        }else{
                        System.out.println(cursable.listarAsistencias());
                        }
                    } catch (AlumnosVacioException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        sesion.agregarPresente(cursable, elegirOpcion(cursable.getElegibles()));
                    } catch (NoSeEncontroElElementoException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("No se pudo guardar la asistencia en la base de datos");
                    }
                    break;
                case 4:
                    try {
                        sesion.eliminarPresente(cursable, elegirOpcion(cursable.getElegibles()));
                    } catch (NoSeEncontroElElementoException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("No se pudo guardar la asistencia en la base de datos");
                    }
                    break;
                case 5:
                    break;
            }
        }while (opcion != 5);
    }

    // Terminado
    public void menuEditarMateria(Materia materia){
        int opcion;
        do {
            System.out.println("---EDITAR MATERIA---\n");
            System.out.println("1 - Crear Cursable");
            System.out.println("2 - Borrar Cursable");
            System.out.println("3 - Cambiar Nombre");
            System.out.println("4 - Editar Cursable");
            System.out.println("5 - Atrás");
            opcion = leerNumeroPositivoMinMax(1, 5);

            switch (opcion) {
                case 1:
                    switch (materia.getTipoMateria()) {
                        case PARTICULAR:

                            try {
                                sesion.agregarCursable(materia, crearClaseParticular());
                            } catch (ElementoRepetidoException e) {
                                System.out.println(e.getMessage());
                            } catch (SQLException e) {
                                System.out.println("No se pudo guardar la clase en la base de datos");
                            }
                            break;

                        case COMISIONADA:
                            try {
                                sesion.agregarCursable(materia, crearComision());
                            } catch (ElementoRepetidoException e) {
                                System.out.println(e.getMessage());
                            } catch (SQLException e) {
                                System.out.println("No se pudo guardar la comisión en la base de datos");
                            }
                    }
                    break;
                case 2:
                    try {
                        sesion.darBajaCursable(materia, extraerElegibles(materia));
                    } catch (NoSeEncontroElElementoException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("No se pudo eliminar el cursable de la base de datos");
                    }
                    break;
                case 3:
                    try {
                        sesion.actualizarNombreMateria(materia, leerNombre("Escriba el nuevo nombre"));
                    } catch (SQLException e) {
                        System.out.println("No se pudo actualizar la materia en la base de datos");
                    }
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
        }while (opcion != 5);

    }

    // Terminado
    public void menuCursable(ICursable curso) {
        int opcion;
        do {
            System.out.println("--EDITAR CURSABLE--");
            System.out.println("1 - Agregar Alumno");
            System.out.println("2 - Eliminar Alumno");
            System.out.println("3 - Cambiar Nombre");
            System.out.println("4 - Atrás");
            opcion = leerNumeroPositivoMinMax(1, 4);

            switch (opcion) {
                case 1:
                    try {
                        sesion.agregarAlumnoACursable(
                                curso, elegirOpcion(sesion.getAlumnosElegibles()));
                    } catch (ElementoRepetidoException e) {
                        System.out.println(e.getMessage());
                    } catch (AlumnosVacioException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("No se pudo guardar el alumno en la base de datos");
                    }
                    break;
                case 2:
                    try {
                        sesion.eliminarAlumnoDeCursable(
                                curso, elegirOpcion(sesion.getAlumnosElegibles()));
                    } catch (NoSeEncontroElElementoException e) {
                        System.out.println(e.getMessage());
                    } catch (AlumnosVacioException e) {
                        System.out.println(e.getMessage());
                    } catch (SinElementosException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("No se pudo eliminar el alumno de la base de datos");
                    }
                    break;
                case 3:
                    try {
                        sesion.actualizarNombreCursable(
                                curso, menu.Verificador.leerNombre("Escriba el nuevo nombre"));
                    } catch (SQLException e) {
                        System.out.println("No se pudo actualizar el cursable en la base de datos");
                    }
                    break;
                case 4:
                    break;
            }
        }while (opcion != 4);
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
            System.out.println(numero++ + " - " + generiquito.getNombreElegible());
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
            } catch (SQLException e){
                System.out.println("No se pudo guardar la materia en la base de datos");
            } catch (ElementoRepetidoException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void eliminarMateria(Materia materia){
        try{
            sesion.darBajaMateria(materia);
            System.out.println("Materia eliminada");
        }catch (NoSeEncontroElElementoException e){
            System.out.println(e.getMessage());
        }catch (SQLException e){
            System.out.println("No se pudo eliminar de la base de datos");
        }
    }

    public Comision crearComision(){

        String nombre = menu.Verificador.leerNombre("Ingrese el nombre de la comision: ");
        System.out.println("Ingrese año de la cursada: ");
        int anio = menu.Verificador.leerNumeroPositivoMinMax(2026,2050);

        return new Comision(nombre, anio);

    }

    public ClaseIndividual crearClaseParticular(){
        System.out.println("¿A que hora se cursa esta clase? (solo hora en punto de 1 a 23)");
        System.out.println("Ten en cuenta que la hora no se puede cambiar");
        int hora = menu.Verificador.leerNumeroPositivoMinMax(1,23);

        System.out.println("Ingrese el mes(1/12): ");
        int mes = menu.Verificador.leerNumeroPositivoMinMax(1,12);
        int dia = menu.Verificador.pedirDiaSegunMes(mes, queAnioEs());

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
        } catch (SQLException e) {
            System.out.println("No se pudo guardar el alumno en la base de datos " +  e.getMessage());
        }
    }

    private void tomarAsistenciaPersistida(ICursable cursable) {
        try {
            if (cursable.getElegibles().isEmpty()) {
                throw new AlumnosVacioException("No hay alumnos asignados a la clase");
            }
            if (cursable instanceof Comision comision
                    && comision.getAsistencias().containsKey(LocalDate.now())) {
                throw new AsistenciaYaTomadaException("La asistencia ya ha sido tomada hoy");
            }
            if (cursable instanceof ClaseIndividual clase && clase.isAsistenciaTomada()) {
                throw new AsistenciaYaTomadaException(
                        "La asistencia de esta clase ya está tomada");
            }

            HashSet<Alumno> presentes = new HashSet<>();
            for (Alumno alumno : cursable.getElegibles()) {
                if (consultarAlumno(alumno)) {
                    presentes.add(alumno);
                }
            }
            sesion.guardarAsistencia(cursable, presentes);
        } catch (AlumnosVacioException | AsistenciaYaTomadaException | SinElementosException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("No se pudo guardar la asistencia en la base de datos");
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
