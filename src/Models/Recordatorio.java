package Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Recordatorio that)) return false;
        return Objects.equals(descripcion, that.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descripcion);
    }

    public ArrayList<String> getMaterialesEnUso() {
        return materialesEnUso;
    }

    public void setMaterialesEnUso(ArrayList<String> materialesEnUso) {
        this.materialesEnUso = materialesEnUso;
    }

    public Date getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(Date fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<String> getStockMateriales() {
        return stockMateriales;
    }

    public void setStockMateriales(ArrayList<String> stockMateriales) {
        this.stockMateriales = stockMateriales;
    }
}
