package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Vivienda;

import java.io.IOException;
import java.util.List;

public interface ViviendaDAO {
    List<Vivienda> cargarViviendas() throws IOException;

    void guardarViviendas(List<Vivienda> viviendas) throws IOException;
}