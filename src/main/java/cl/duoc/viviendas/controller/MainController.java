package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.ConEstacionamiento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Vivienda> tblViviendas;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, String> colDetalle;
    @FXML private TableColumn<Vivienda, String> colEstacionamiento;
    @FXML private TableColumn<Vivienda, Double> colCosto;
    @FXML private Label lblMensaje;

    private ViviendaRepository repository;
    private final ObservableList<Vivienda> viviendas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        repository = new ViviendaRepository();
        try {
            repository.cargar();
            viviendas.setAll(repository.listar());
        } catch (IOException e) {
            mostrarMensaje("Error al leer los archivos: " + e.getMessage(), true);
        }

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costoArriendo"));

        colEstacionamiento.setCellValueFactory(celda -> {
            Vivienda v = celda.getValue();
            String texto = "—";
            if (v instanceof ConEstacionamiento ce) {
                texto = ce.tieneEstacionamientoAsignado() ? "Sí" : "No";
            }
            return new SimpleStringProperty(texto);
        });

        FilteredList<Vivienda> filteredData = new FilteredList<>(viviendas, p -> true);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(vivienda -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String filter = newValue.toLowerCase().trim();
                return vivienda.getCodigo().toLowerCase().contains(filter)
                        || vivienda.getTipo().toLowerCase().contains(filter);
            });
        });

        SortedList<Vivienda> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblViviendas.comparatorProperty());
        tblViviendas.setItems(sortedData);

        // Orden inicial por el número del código (001, 002, 003...)
        colCodigo.setComparator((a, b) -> {
            int cmp = Integer.compare(numeroDeCodigo(a), numeroDeCodigo(b));
            return cmp != 0 ? cmp : a.compareToIgnoreCase(b);
        });
        colCodigo.setSortType(TableColumn.SortType.ASCENDING);
        tblViviendas.getSortOrder().add(colCodigo);
    }

    @FXML
    private void handleNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cl/duoc/viviendas/view/registro-view.fxml"));
            Parent root = loader.load();

            RegistroController registroController = loader.getController();
            registroController.setRepository(repository);

            Stage stage = new Stage();
            stage.setTitle("Registrar Nueva Vivienda");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 440, 530));
            stage.setResizable(false);
            stage.showAndWait();

            viviendas.setAll(repository.listar());

            Vivienda nueva = registroController.getViviendaRegistrada();
            if (nueva != null) {
                mostrarMensaje(nueva.getCodigo() + " (" + nueva.getTipo() + ") registrada correctamente.", false);
            } else {
                mostrarMensaje("Registro cancelado.", false);
            }
        } catch (IOException e) {
            mostrarMensaje("Error al abrir la ventana de registro.", true);
        }
    }

    @FXML
    private void handleEliminar() {
        Vivienda seleccionada = tblViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Seleccione una vivienda para eliminar.", true);
            return;
        }

        try {
            repository.eliminar(seleccionada);
            mostrarMensaje("Vivienda '" + seleccionada.getCodigo() + "' eliminada correctamente.", false);
        } catch (IOException e) {
            mostrarMensaje("No se pudo guardar la eliminación: " + e.getMessage(), true);
        } catch (IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
        viviendas.setAll(repository.listar());
    }

    private static int numeroDeCodigo(String codigo) {
        int fin = codigo.length();
        int inicio = fin;
        while (inicio > 0 && Character.isDigit(codigo.charAt(inicio - 1))) {
            inicio--;
        }
        if (inicio == fin) {
            return Integer.MAX_VALUE;   // códigos sin número van al final
        }
        try {
            return Integer.parseInt(codigo.substring(inicio, fin));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private void mostrarMensaje(String texto, boolean error) {
        lblMensaje.setTextFill(Color.web(error ? "#e74c3c" : "#27ae60"));
        lblMensaje.setText(texto);
    }
}