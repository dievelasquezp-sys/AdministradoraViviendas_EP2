package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.ViviendaDAO;
import cl.duoc.viviendas.dao.ViviendaDAOImpl;
import cl.duoc.viviendas.model.Vivienda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ViviendaRepository {
    private final ViviendaDAO viviendaDAO;
    private final ObservableList<Vivienda> viviendas;

    public ViviendaRepository() {
        this.viviendaDAO = new ViviendaDAOImpl();
        this.viviendas = FXCollections.observableArrayList(viviendaDAO.cargarViviendas());
    }

    public ObservableList<Vivienda> getViviendas() {
        return viviendas;
    }

    public boolean agregar(Vivienda vivienda) {
        for (Vivienda v : viviendas) {
            if (v.getCodigo().equalsIgnoreCase(vivienda.getCodigo())) {
                return false;
            }
        }
        viviendas.add(vivienda);
        return viviendaDAO.guardarViviendas(viviendas);
    }

    public boolean eliminar(Vivienda vivienda) {
        boolean eliminado = viviendas.remove(vivienda);
        if (eliminado) {
            viviendaDAO.guardarViviendas(viviendas);
        }
        return eliminado;
    }
}