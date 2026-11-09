package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    @FXML private Label lblError;

    private ViviendaRepository repository;

    public void setRepository(ViviendaRepository repository) {
        this.repository = repository;
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
    }

    @FXML
    private void handleGuardar() {
        lblError.setText("");

        String codigo = txtCodigo.getText().trim();
        String strSuperficie = txtSuperficie.getText().trim();
        String strHabitaciones = txtHabitaciones.getText().trim();
        String tipo = cmbTipo.getValue();

        if (codigo.isEmpty() || strSuperficie.isEmpty() || strHabitaciones.isEmpty()) {
            lblError.setText("Error: Código, superficie y habitaciones son obligatorios.");
            return;
        }

        double superficie;
        int habitaciones;
        try {
            superficie = Double.parseDouble(strSuperficie);
            habitaciones = Integer.parseInt(strHabitaciones);
        } catch (NumberFormatException e) {
            lblError.setText("Error: Superficie y habitaciones deben ser numéricos.");
            return;
        }

        Vivienda vivienda;

        try {
            if ("Casa".equalsIgnoreCase(tipo)) {
                boolean tienePatio = chkPatio.isSelected();
                vivienda = new Casa(codigo, superficie, habitaciones, tienePatio);
            } else {
                String strPiso = txtPiso.getText().trim();
                if (strPiso.isEmpty()) {
                    lblError.setText("Error: Ingrese el número de piso.");
                    return;
                }
                int piso = Integer.parseInt(strPiso);
                boolean gastoComunAlDia = chkGastoComun.isSelected();
                vivienda = new Departamento(codigo, superficie, habitaciones, piso, gastoComunAlDia);
            }
        } catch (IllegalArgumentException e) {
            lblError.setText("Error: " + e.getMessage());
            return;
        } catch (Exception e) {
            lblError.setText("Error inesperado en los datos ingresados.");
            return;
        }

        if (repository != null) {
            boolean exito = repository.agregar(vivienda);
            if (!exito) {
                lblError.setText("Error: Ya existe una vivienda con el código '" + codigo + "'.");
                return;
            }
            cerrarVentana();
        }
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
