package ui.pantallas.mensajeriaOnline;

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
import ui.pantallas.mensajeria.MensajeriaViewModel;

public class MensajeriaOnlineController extends BasePantallaController {
    private MensajeriaOnlineViewModel mensajeriaOnlineViewModel;
    @FXML
    private TableView<MensajeCaja> tablaMensajes;
    @FXML
    private TableColumn<MensajeCaja, String> mensaje;
    @FXML
    private TextArea txtMensaje;
    @FXML
    private MFXTextField txtContrasena;
    @FXML
    private TextField txtNombreCarpeta;
    @FXML
    private MFXTextField txtUsuario;


    @Inject
    public MensajeriaOnlineController(MensajeriaOnlineViewModel mensajeriaOnlineViewModel) {
        this.mensajeriaOnlineViewModel = mensajeriaOnlineViewModel;
    }
    public void initialize(){
        mensaje.setCellValueFactory(new PropertyValueFactory<>(ConstantesPantallas.MENSAJE));
        mensajeriaOnlineViewModel.getState().addListener((observableValue, oldState, newState) -> {
            if (newState.getError() != null) {
                this.getPrincipalController().sacarAlertError(newState.getError());
                mensajeriaOnlineViewModel.limpiarEstado();
            }
            if (newState.getMensajesCajas() != null) {
                tablaMensajes.getItems().setAll(newState.getMensajesCajas());
                this.getPrincipalController().sacarAlertInformacion(ConstantesPantallas.ACCION_REALIZADA_CON_EXITO);
                mensajeriaOnlineViewModel.limpiarEstado();
            }
        });
    }
    @FXML
    private void updateMensaje(){
        MensajeCaja mensajeCaja = tablaMensajes.getSelectionModel().getSelectedItem();
        if (mensajeCaja == null || txtMensaje.getText().isEmpty() || txtContrasena.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.TIENES_QUE_ELEGIR_UN_MENSAJE);
        } else {
            mensajeCaja.setMensaje(txtMensaje.getText());
            mensajeriaOnlineViewModel.updateMensaje(mensajeCaja,txtContrasena.getText());
            txtMensaje.clear();
        }
    }
    @FXML
    private void getMensajesDeCarpeta(){
        if (txtUsuario.getText().isEmpty() || txtContrasena.getText().isEmpty() || txtNombreCarpeta.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.TIENES_QUE_ELEGIR_UN_MENSAJE);
        } else {
            mensajeriaOnlineViewModel.getMensajesCajas(txtUsuario.getText(),txtNombreCarpeta.getText(),txtContrasena.getText());
            txtMensaje.clear();
        }
    }
}
