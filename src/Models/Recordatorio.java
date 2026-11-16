package Models;

import java.util.ArrayList;
import java.util.Date;

public class Recordatorio {
    private ArrayList<String> materialesEnUso;
    private Date fechaYHora;
    private String descripcion;
    private ArrayList<String> stockMateriales;

    public Recordatorio(Date fechaYHora, String descripcion) {
        this.fechaYHora = fechaYHora;
        this.descripcion = descripcion;
        this.materialesEnUso = new ArrayList<>();
    }

}
