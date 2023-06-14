package ui.pantallas.mensajeria;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import ui.pantallas.common.BasePantallaController;
import ui.pantallas.common.ConstantesPantallas;

public class MensajeriaController extends BasePantallaController {
    private MensajeriaViewModel mensajeriaViewModel;
    @FXML
    private TableView<MensajeCaja> tablaMensajes;
    @FXML
    private TableColumn<MensajeCaja, String> mensaje;
    @FXML
    private MFXComboBox<String> carpetas;
    @FXML
    private MFXComboBox<String> modoCarpeta;
    @FXML
    private TextArea txtMensaje;
    @FXML
    private MFXTextField txtContrasena;
    @FXML
    private TextField txtNombreCarpeta;
    @FXML
    private TextField txtContrasenaNueva;
    private String user;

    private String constraseñaCaja;

    @Inject
    public MensajeriaController(MensajeriaViewModel mensajeriaViewModel) {
        this.mensajeriaViewModel = mensajeriaViewModel;
    }
    public void initialize(){
        mensaje.setCellValueFactory(new PropertyValueFactory<>(ConstantesPantallas.MENSAJE));
        modoCarpeta.getItems().addAll(ConstantesPantallas.EDICION, ConstantesPantallas.LECTURA);
        mensajeriaViewModel.getState().addListener((observableValue, oldState, newState) -> {
            if (newState.getError() != null) {
                this.getPrincipalController().sacarAlertError(newState.getError());
                mensajeriaViewModel.limpiarEstado();
            }
            if (newState.getMensajesCajas() != null) {
                tablaMensajes.getItems().setAll(newState.getMensajesCajas());
                mensajeriaViewModel.limpiarEstado();
            }
            if (newState.getCarpetas() != null) {
                carpetas.getItems().setAll(newState.getCarpetas());
                if (newState.getCarpetas().isEmpty()) {
                    this.getPrincipalController().sacarAlertError(ConstantesPantallas.NO_HAY_CARPETAS_TE_RECOMIENDO_CREAR_UNA);
                }else {
                    this.getPrincipalController().sacarAlertInformacion(ConstantesPantallas.LAS_CAJAS_HAN_SIDO_CARGADAS_EN_EL_COMBO_BOX);
                }
                mensajeriaViewModel.limpiarEstado();
            }
            if (newState.isOk()) {
                this.getPrincipalController().sacarAlertInformacion(ConstantesPantallas.OPERACION_REALIZADA_CON_EXITO);
                mensajeriaViewModel.limpiarEstado();
            }
        });
    }
    public void principalCargado() {
        user = this.getPrincipalController().getLogin();
        mensajeriaViewModel.cargarCarpertas(user);
    }
    @FXML
    public void filtrarPorCaja(){
        if (carpetas.getValue() != null && !txtContrasena.getText().isEmpty()) {
            mensajeriaViewModel.getMensajesCajas(carpetas.getValue(),user,txtContrasena.getText());
            constraseñaCaja = txtContrasena.getText();
        }else {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.NO_SE_HA_SELECCIONADO_NINGUNA_CARPETA_O_NO_SE_HA_INTRODUCIDO_LA_CONTRASEÑA);
        }

    }
    @FXML
    private void añadirMensaje(){
        if (txtMensaje.getText().isEmpty() || carpetas.getValue() == null) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.EL_MENSAJE_NO_PUEDE_ESTAR_VACIO_Y_TIENES_QUE_ELEGIR_UNA_CARPETA);
        } else {
            MensajeCaja mensajeCaja = new MensajeCaja(txtMensaje.getText(), carpetas.getValue(), user);
            mensajeriaViewModel.añadirMensaje(mensajeCaja,constraseñaCaja);
            txtMensaje.clear();
        }
    }
    @FXML
    private void updateMensaje(){
        MensajeCaja mensajeCaja = tablaMensajes.getSelectionModel().getSelectedItem();
        if (mensajeCaja == null || txtMensaje.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.TIENES_QUE_ELEGIR_UN_MENSAJE);
        } else {
            mensajeCaja.setMensaje(txtMensaje.getText());
            mensajeriaViewModel.updateMensaje(mensajeCaja,constraseñaCaja);
            txtMensaje.clear();
        }
    }
    @FXML
    private void eliminarMensaje(){
        MensajeCaja mensajeCaja = tablaMensajes.getSelectionModel().getSelectedItem();
        if (mensajeCaja == null) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.TIENES_QUE_ELEGIR_UN_MENSAJE);
        } else {
            mensajeriaViewModel.eliminarMensaje(mensajeCaja,constraseñaCaja);
        }
    }
    @FXML
    private void añadirCarpeta(){
        if (txtNombreCarpeta.getText().isEmpty() || txtContrasena.getText().isEmpty() || modoCarpeta.getValue() == null) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.NECESITAS_RELLENAR_LOS_CAMPOS_DE_CONTRASEÑAS_Y_LA_CARPETA);
        } else {
            Carpeta carpeta = new Carpeta(txtNombreCarpeta.getText(),user, txtContrasena.getText(), modoCarpeta.getValue());
            mensajeriaViewModel.añadirCarpeta(carpeta);
            txtNombreCarpeta.clear();
        }
    }
    @FXML
    private void cambiarContrasena(){
        if (txtNombreCarpeta.getText().isEmpty() || txtContrasena.getText().isEmpty() || txtContrasenaNueva.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.NECESITAS_RELLENAR_LOS_CAMPOS_DE_CONTRASEÑAS_Y_LA_CARPETA);
        } else {
            Carpeta carpeta = new Carpeta(txtNombreCarpeta.getText(),user, txtContrasena.getText());
            mensajeriaViewModel.cambiarContrasena(carpeta, txtContrasenaNueva.getText());
            txtNombreCarpeta.clear();
            txtContrasenaNueva.clear();
        }
    }
}
