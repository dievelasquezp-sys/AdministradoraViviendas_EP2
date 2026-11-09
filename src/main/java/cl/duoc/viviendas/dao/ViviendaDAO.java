package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Vivienda;
import java.util.List;

public interface ViviendaDAO {
    List<Vivienda> cargarViviendas();
    boolean guardarViviendas(List<Vivienda> viviendas);
}


