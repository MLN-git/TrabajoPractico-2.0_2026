package Models;

import Enums.ETipoMateria;
import Exceptions.ElementoRepetidoException;
import Exceptions.NoSeEncontroElElementoException;
import Interfaces.ICursable;
import Interfaces.IElegible;
import Interfaces.ITieneNombre;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class Materia implements IElegible<ICursable>, ITieneNombre {
    private UUID id;
    private String nombre;
    private HashSet<ICursable> iCursables;
    private ETipoMateria tipoMateria;

    public Materia(String nombre,  ETipoMateria tipoMateria) {
        this.nombre = nombre;
        this.id = UUID.randomUUID();
        iCursables = new HashSet<ICursable>();
        this.tipoMateria = tipoMateria;
    }

    public Materia(UUID id, String nombre,  ETipoMateria tipoMateria) {
        this.id = id;
        this.nombre = nombre;
        this.iCursables = new HashSet<>();
        this.tipoMateria = tipoMateria;
    }

    public void agregarCursable(ICursable curso) throws ElementoRepetidoException {
        if (!(iCursables.add(curso))){
            throw new ElementoRepetidoException("El cursable está repetido");
        }
    }

    public void eliminarCursable(ICursable curso) throws NoSeEncontroElElementoException {
        if (!(iCursables.remove(curso))){
            throw new NoSeEncontroElElementoException("El Cursable no ha sido encontrado");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Materia materia)) return false;
        return Objects.equals(id, materia.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreElegible() {
        return nombre;
    }

    public ETipoMateria getTipoMateria() {
        return tipoMateria;
    }

    public void setTipoMateria(ETipoMateria tipoMateria) {
        this.tipoMateria = tipoMateria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public HashSet<ICursable> getICursables() {
        return iCursables;
    }

    @Override
    public ArrayList<ICursable> getElegibles() {
        return new ArrayList<ICursable>(iCursables);
    }

    public void setICursables(HashSet<ICursable> aulas) {
        iCursables = aulas;
    }
}
