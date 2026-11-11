package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.ViviendaDAO;
import cl.duoc.viviendas.dao.ViviendaDAOImpl;
import cl.duoc.viviendas.model.Vivienda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {

    private final ViviendaDAO viviendaDAO = new ViviendaDAOImpl();
    private final List<Vivienda> viviendas = new ArrayList<>();

    public void cargar() throws IOException {
        viviendas.clear();
        viviendas.addAll(viviendaDAO.cargarViviendas());
    }

    public List<Vivienda> listar() {
        return List.copyOf(viviendas);
    }

    public void agregar(Vivienda vivienda) throws IOException {
        for (Vivienda v : viviendas) {
            if (v.getCodigo().equalsIgnoreCase(vivienda.getCodigo())) {
                throw new IllegalArgumentException(
                        "Ya existe una vivienda con el código '" + vivienda.getCodigo() + "'.");
            }
        }
        viviendas.add(vivienda);
        try {
            viviendaDAO.guardarViviendas(viviendas);
        } catch (IOException e) {
            viviendas.remove(vivienda);   // memoria y archivo siguen iguales
            throw e;
        }
    }

    public void eliminar(Vivienda vivienda) throws IOException {
        int indice = viviendas.indexOf(vivienda);
        if (indice < 0) {
            throw new IllegalArgumentException("La vivienda no está registrada.");
        }
        viviendas.remove(indice);
        try {
            viviendaDAO.guardarViviendas(viviendas);
        } catch (IOException e) {
            viviendas.add(indice, vivienda);   // se revierte si falla el guardado
            throw e;
        }
    }
}