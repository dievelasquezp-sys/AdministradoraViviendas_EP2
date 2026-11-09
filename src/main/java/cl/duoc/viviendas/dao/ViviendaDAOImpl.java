package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Vivienda;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ViviendaDAOImpl implements ViviendaDAO {
    private static final String FILE_PATH = "viviendas.dat";

    @Override
    @SuppressWarnings("unchecked")
    public List<Vivienda> cargarViviendas() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Vivienda>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean guardarViviendas(List<Vivienda> viviendas) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(viviendas));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
