package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistroController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;

    @FXML private Label lblPatio;
    @FXML private CheckBox chkPatio;

    @FXML private Label lblPiso;
    @FXML private TextField txtPiso;
    @FXML private Label lblGastoComun;
    @FXML private CheckBox chkGastoComun;
    @FXML private Label lblEstacionamiento;
    @FXML private CheckBox chkEstacionamiento;

    @FXML private Label lblError;

    private ViviendaRepository repository;
    private Vivienda viviendaRegistrada;

    public void setRepository(ViviendaRepository repository) {
        this.repository = repository;
    }

    public Vivienda getViviendaRegistrada() {
        return viviendaRegistrada;
    }

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList("Casa", "Departamento"));
        cmbTipo.getSelectionModel().selectFirst();
        actualizarVisibilidadCampos("Casa");

        cmbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actualizarVisibilidadCampos(newVal);
            }
        });
    }

    private void actualizarVisibilidadCampos(String tipo) {
        boolean esCasa = "Casa".equalsIgnoreCase(tipo);

        lblPatio.setVisible(esCasa);
        lblPatio.setManaged(esCasa);
        chkPatio.setVisible(esCasa);
        chkPatio.setManaged(esCasa);

        lblPiso.setVisible(!esCasa);
        lblPiso.setManaged(!esCasa);
        txtPiso.setVisible(!esCasa);
        txtPiso.setManaged(!esCasa);

        lblGastoComun.setVisible(!esCasa);
        lblGastoComun.setManaged(!esCasa);
        chkGastoComun.setVisible(!esCasa);
        chkGastoComun.setManaged(!esCasa);

        lblEstacionamiento.setVisible(!esCasa);
        lblEstacionamiento.setManaged(!esCasa);
        chkEstacionamiento.setVisible(!esCasa);
        chkEstacionamiento.setManaged(!esCasa);
    }

    @FXML
    private void handleGuardar() {
        lblError.setText("");

        String tipo = cmbTipo.getValue();
        String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim();
        String strSuperficie = txtSuperficie.getText() == null ? "" : txtSuperficie.getText().trim();
        String strHabitaciones = txtHabitaciones.getText() == null ? "" : txtHabitaciones.getText().trim();

        if (tipo == null) {
            lblError.setText("Error en Tipo: debe seleccionar un tipo de vivienda.");
            return;
        }
        if (codigo.isEmpty()) {
            lblError.setText("Error en Código: no puede estar vacío ni contener solo espacios.");
            return;
        }

        double superficie;
        try {
            superficie = Double.parseDouble(strSuperficie.replace(',', '.'));
        } catch (NumberFormatException e) {
            lblError.setText("Error en Superficie: debe ser un número (acepta decimales).");
            return;
        }
        if (superficie < 20 || superficie > 500) {
            lblError.setText("Error en Superficie: debe estar entre 20 y 500 metros cuadrados.");
            return;
        }

        int habitaciones;
        try {
            habitaciones = Integer.parseInt(strHabitaciones);
        } catch (NumberFormatException e) {
            lblError.setText("Error en Habitaciones: debe ser un número entero.");
            return;
        }
        if (habitaciones <= 0) {
            lblError.setText("Error en Habitaciones: debe ser mayor que cero.");
            return;
        }

        Vivienda vivienda;
        try {
            if ("Casa".equalsIgnoreCase(tipo)) {
                vivienda = new Casa(codigo, superficie, habitaciones, chkPatio.isSelected());
            } else {
                String strPiso = txtPiso.getText() == null ? "" : txtPiso.getText().trim();
                int piso;
                try {
                    piso = Integer.parseInt(strPiso);
                } catch (NumberFormatException e) {
                    lblError.setText("Error en Piso: debe ser un número entero.");
                    return;
                }
                if (piso <= 0) {
                    lblError.setText("Error en Piso: debe ser mayor que cero.");
                    return;
                }
                Departamento departamento = new Departamento(codigo, superficie, habitaciones, piso,
                        chkGastoComun.isSelected());
                if (chkEstacionamiento.isSelected()) {
                    departamento.asignarEstacionamiento();
                }
                vivienda = departamento;
            }
        } catch (IllegalArgumentException e) {
            lblError.setText("Error: " + e.getMessage());
            return;
        }

        try {
            repository.agregar(vivienda);
        } catch (IllegalArgumentException e) {
            lblError.setText("Error en Código: " + e.getMessage());
            return;
        } catch (IOException e) {
            lblError.setText("Error: no se pudo guardar en los archivos. " + e.getMessage());
            return;
        }

        viviendaRegistrada = vivienda;
        cerrarVentana();
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtCodigo.getScene().getWindow();
        stage.close();
    }
}