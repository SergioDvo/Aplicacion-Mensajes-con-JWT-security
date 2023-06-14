package ui.pantallas.login;

import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import lombok.extern.log4j.Log4j2;
import modelo.utils.Login;
import retrofit.di.utils.CacheAuthorization;
import ui.pantallas.common.BasePantallaController;
import ui.pantallas.common.ConstantesPantallas;

import java.security.PublicKey;

@Log4j2
public class LoginController extends BasePantallaController {

    private LoginViewModel loginViewModel;
    @FXML
    private MFXTextField txtUserName;
    @FXML
    private MFXTextField txtPassword;

    @FXML
    private MFXTextField txtMail;
    private CacheAuthorization ca;


    @Inject
    public LoginController(LoginViewModel loginViewModel,CacheAuthorization ca) {
        this.ca = ca;
        this.loginViewModel = loginViewModel;
    }

    public void initialize() {
        loginViewModel.getState().addListener((observableValue, oldState, newState) -> {
            if (newState.getError() != null) {
                this.getPrincipalController().sacarAlertInformacion(newState.getError());
                loginViewModel.limpiarState();
            }
            if (newState.getLogin() != null) {
                this.getPrincipalController().setLogin(newState.getLogin().getNombre());
                this.getPrincipalController().onLoginHecho();
            }
            //todo crear privada tambien del servidor para firmar certifacaos
            //despues devolver el certificado guardando con la clave del servidor
            //al hacer login se signa con la privada del usuario y se envia el certificado
            //para guardar en la keystore del cliente se guarda la privada y el certficoado y la contraseña es la contraseña que el pone

            //CREAR CARPETA
            //cifrar la contraseña de la carpeta con la clave publica del usuario
            //y luego se descifra tambien en cliente una vez ya llamada al servidor

            //COMPARTIR CARPETA
            //llamas a seridopr y coger la carpeta con la contraseña
            //guardas en base de datos una contraseña de la carpeta cifrada con la clave publica del nuevo usuario
            //para poder ver la publica del otro usuario miras su certificado y para ver si es original y no falso lo miras con la publica del servidor
        });
        //todo crear cable publica del servidor aqui verifando si ya hay una
        //todo

    }
    @FXML
    private void doLogin() {
        if (txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.DEBE_RELLENAR_EL_USUARIO_Y_LA_CONTRASEÑA);
        } else {
            loginViewModel.doLogin(txtUserName.getText(), txtPassword.getText());
        }
    }
    @FXML
    private void doRegister() {
        if (txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty() || ca.getPk() == null) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.DEBE_RELLENAR_TODOS_LOS_CAMPOS);
        } else {
            Login login = new Login(txtUserName.getText(), txtPassword.getText());
            loginViewModel.doRegister(login, ca.getPk());
        }
    }
    @FXML
    private void getClavePublica(){
        loginViewModel.getClavePublicaServidor();
    }
    @FXML
    private void recuperarContraseña() {
        if (txtUserName.getText().isEmpty() || txtMail.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.DEBE_RELLENAR_EL_USUARIO_Y_EL_EMAIL);
        } else {
            loginViewModel.reenviarCodigo(txtMail.getText(), txtUserName.getText());
        }
    }

    @FXML
    private void reenviarCorreo() {
        if (txtMail.getText().isEmpty() || txtUserName.getText().isEmpty()) {
            this.getPrincipalController().sacarAlertError(ConstantesPantallas.DEBE_RELLENAR_EL_CAMPO_DE_CORREO_Y_EL_DE_USUARIO);
        } else {
            loginViewModel.reenviarCorreo(txtMail.getText(), txtUserName.getText());
        }
    }

}
