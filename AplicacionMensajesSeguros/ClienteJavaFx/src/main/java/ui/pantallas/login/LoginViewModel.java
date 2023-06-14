package ui.pantallas.login;

import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import modelo.utils.Login;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import retrofit.di.utils.CacheAuthorization;
import servicios.ServiciosLogin;
import ui.pantallas.common.ConstantesPantallas;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

public class LoginViewModel {


    private final ServiciosLogin serviciosLogin;

    private final CacheAuthorization ca;

    @Inject
    public LoginViewModel(ServiciosLogin serviciosLogin, CacheAuthorization ca) {
        this.serviciosLogin = serviciosLogin;
        this.ca = ca;
        state = new SimpleObjectProperty<>(new LoginState(null,null));
    }
    private final ObjectProperty<LoginState> state;
    public ReadOnlyObjectProperty<LoginState> getState() {
        return state;
    }
    public void doLogin(String username,String password) {
        serviciosLogin.doLogin(username,password)
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(either -> {
                    LoginState ls = null;
                    if (either.isLeft())
                        ls = new LoginState(null, either.getLeft());
                    else
                        ls = new LoginState(either.get(), null);
                    state.setValue(ls);
                });
    }
    public void doRegister(Login login,PublicKey clavePublicaServidor) {
        serviciosLogin.doRegister(login,clavePublicaServidor)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    LoginState ls = null;
                    if (either.isLeft())
                        ls = new LoginState(null, either.getLeft());
                    else {
                        String resultado = serviciosLogin.guardarKeysCliente(either.get(),clavePublicaServidor);
                        ls = new LoginState(null, resultado);
                    }
                    state.setValue(ls);
                });
    }
    public void limpiarState() {
        state.setValue(new LoginState(null, null));
    }
    public void reenviarCorreo(String email, String username) {
        serviciosLogin.reenviarCodigo(email, username)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(s -> {
                    if (s.equals(ConstantesPantallas.NO_CONTENT))
                        state.setValue(new LoginState(null, ConstantesPantallas.CORREO_REENVIADO_CORRECTAMENTE));
                    else
                            state.setValue(new LoginState(null, ConstantesPantallas.EL_USUARIO_NO_EXISTE_O_NO_TIENE_ESE_CORREO_ASOCIADO));
                    });
                }
    public void reenviarCodigo(String email, String username) {
        serviciosLogin.cambiarContraseña(email, username)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(s -> {
                    if (s.equals(ConstantesPantallas.NO_CONTENT))
                        state.setValue(new LoginState(null, ConstantesPantallas.REVISA_TU_CORREO_PARA_CAMBIAR_TU_CONTRASEÑA));
                    else
                        state.setValue(new LoginState(null, ConstantesPantallas.EL_USUARIO_NO_EXISTE_O_NO_TIENE_ESE_CORREO_ASOCIADO));
                });
    }
    public void getClavePublicaServidor() {
        serviciosLogin.getClavePublicaServer()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(s -> {
                    if (s.isLeft())
                        state.setValue(new LoginState(null, s.getLeft()));
                    else {
                        KeyFactory keyFactoryRSA = KeyFactory.getInstance("RSA");
                        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(s.get().get(0)));
                        ca.setPk(keyFactoryRSA.generatePublic(publicKeySpec));
                    }
                });

    }
}


