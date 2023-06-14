package ui.pantallas.anadirUsuario;

import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.utils.Login;
import ui.pantallas.common.BasePantallaController;
import ui.pantallas.common.ConstantesPantallas;

public class AnadirUsuarioController extends BasePantallaController {
    private AnadirUsuarioViewModel anadirUsuarioViewModel;
    @FXML
    private TableView<Login> tablaUsuarios;
    @FXML
    private TableColumn<Login,String> nombre;
    @FXML
    private MFXTextField txtNombre;
    @FXML
    private MFXTextField txtContrasena;

    @Inject
    public AnadirUsuarioController(AnadirUsuarioViewModel anadirUsuarioViewModel) {
        this.anadirUsuarioViewModel = anadirUsuarioViewModel;
    }
    public void initialize(){
        nombre.setCellValueFactory(new PropertyValueFactory<>(ConstantesPantallas.NOMBRE));
        anadirUsuarioViewModel.getState().addListener((observableValue, oldState, newState) -> {
            if (newState.getError() != null) {
                this.getPrincipalController().sacarAlertError(newState.getError());
                anadirUsuarioViewModel.limpiarEstado();
            }
            if (newState.getListaUsuarios() != null) {
                tablaUsuarios.getItems().setAll(newState.getListaUsuarios());
                this.getPrincipalController().sacarAlertInformacion(ConstantesPantallas.ACCION_REALIZADA_CON_EXITO);
                anadirUsuarioViewModel.limpiarEstado();
            }
        });
        anadirUsuarioViewModel.getUsuarios();
    }
    @FXML
    private void añadirUsuario(){
        if (txtNombre.getText().isEmpty() && txtContrasena.getText().isEmpty()){
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.EL_NOMBRE_NI_LA_CONTRASEÑA_PUEDEN_ESTAR_VACIOS);
        }else{
            Login login = new Login(txtNombre.getText(),txtContrasena.getText(), ConstantesPantallas.USER);
            anadirUsuarioViewModel.añadirUsuario(login);
        }
    }


}
