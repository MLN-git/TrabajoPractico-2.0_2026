package Interfaces;

import Exceptions.SinElementosException;

import java.util.ArrayList;

public interface IElegible<T extends ITieneNombre> {
    public ArrayList<T> getElegibles() throws SinElementosException;
}
