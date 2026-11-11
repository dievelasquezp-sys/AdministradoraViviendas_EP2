package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ViviendaDAOImpl implements ViviendaDAO {

    private static final String ARCHIVO_DEPARTAMENTOS = "departamentos.json";
    private static final String ARCHIVO_CASAS = "casas.json";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Vivienda> cargarViviendas() throws IOException {
        List<Vivienda> resultado = new ArrayList<>();
        resultado.addAll(leer(ARCHIVO_DEPARTAMENTOS, Departamento.class));
        resultado.addAll(leer(ARCHIVO_CASAS, Casa.class));
        return resultado;
    }

    @Override
    public void guardarViviendas(List<Vivienda> viviendas) throws IOException {
        List<Departamento> departamentos = viviendas.stream()
                .filter(Departamento.class::isInstance)
                .map(Departamento.class::cast)
                .toList();
        List<Casa> casas = viviendas.stream()
                .filter(Casa.class::isInstance)
                .map(Casa.class::cast)
                .toList();

        escribir(ARCHIVO_DEPARTAMENTOS, departamentos);
        escribir(ARCHIVO_CASAS, casas);
    }

    private <T extends Vivienda> List<T> leer(String archivo, Class<T> tipo) throws IOException {
        Path ruta = Path.of(archivo);
        if (!Files.exists(ruta)) {
            return new ArrayList<>();   // primera ejecución: sin error
        }
        Type tipoLista = TypeToken.getParameterized(List.class, tipo).getType();
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            List<T> lista = gson.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        } catch (JsonParseException e) {
            throw new IOException("El archivo " + archivo + " tiene un formato JSON no válido.", e);
        }
    }

    private void escribir(String archivo, List<?> datos) throws IOException {
        try (Writer writer = Files.newBufferedWriter(Path.of(archivo), StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }
}