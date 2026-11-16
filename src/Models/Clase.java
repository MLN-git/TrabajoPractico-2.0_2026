package Models;

import java.util.Date;
import java.util.Objects;

public class Clase {
    private Date fechaYHora;
    private String nombre;

    public Clase(Date fechaYHora, String nombre) {
        this.fechaYHora = fechaYHora;
        this.nombre = nombre;
    }

    public Date getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(Date fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Clase clase)) return false;
        return Objects.equals(fechaYHora, clase.fechaYHora);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fechaYHora);
    }
}
