package ui.pantallas.anadirUsuario;

import jakarta.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import modelo.utils.Carpeta;
import modelo.utils.Login;
import modelo.utils.MensajeCaja;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import servicios.ServiciosLogin;
import servicios.ServiciosMensaje;
import ui.pantallas.mensajeria.MensajeriaState;

import java.util.List;

public class AnadirUsuarioViewModel { ;
    private final ServiciosLogin serviciosLogin;
    private final ObjectProperty<AnadirUsuarioState> state;

    private List<Login> listaUsuarios;

    @Inject
    public AnadirUsuarioViewModel(ServiciosLogin serviciosLogin) {
        this.serviciosLogin = serviciosLogin;
        this.listaUsuarios = List.of();
        state = new SimpleObjectProperty<>(new AnadirUsuarioState(null, null));
    }
    public ReadOnlyObjectProperty<AnadirUsuarioState> getState() {
        return state;
    }
    public void getUsuarios() {
        serviciosLogin.getUsuarios()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new AnadirUsuarioState(null, either.getLeft()));
                    else {
                        state.setValue(new AnadirUsuarioState(either.get(), null));
                        listaUsuarios = either.get();
                    }
                });
    }
    public void añadirUsuario(Login login) {
        serviciosLogin.añadirUsuario(login)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new AnadirUsuarioState(null, either.getLeft()));
                    else {
                        listaUsuarios.add(login);
                        state.setValue(new AnadirUsuarioState(listaUsuarios, null));

                    }
                });
    }

    public void limpiarEstado() {
        state.set(new AnadirUsuarioState(null, null));
    }

}
