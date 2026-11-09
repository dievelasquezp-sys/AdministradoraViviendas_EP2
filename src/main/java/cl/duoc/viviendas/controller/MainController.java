package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TableColumn<Vivienda, Double> colCosto;
    @FXML private Label lblMensaje;

    private ViviendaRepository repository;

    @FXML
    public void initialize() {
        repository = new ViviendaRepository();

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costoArriendo"));

        FilteredList<Vivienda> filteredData = new FilteredList<>(repository.getViviendas(), p -> true);

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
            stage.setScene(new Scene(root, 440, 480));
            stage.setResizable(false);
            stage.showAndWait();

            lblMensaje.setText("Operación realizada en la lista.");
        } catch (IOException e) {
            lblMensaje.setText("Error al abrir ventana de registro.");
        }
    }

    @FXML
    private void handleEliminar() {
        Vivienda seleccionada = tblViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            lblMensaje.setText("Seleccione una vivienda para eliminar.");
            return;
        }

        boolean borrado = repository.eliminar(seleccionada);
        if (borrado) {
            lblMensaje.setText("Vivienda Código '" + seleccionada.getCodigo() + "' eliminada.");
        } else {
            lblMensaje.setText("No se pudo eliminar la vivienda.");
        }
    }
}