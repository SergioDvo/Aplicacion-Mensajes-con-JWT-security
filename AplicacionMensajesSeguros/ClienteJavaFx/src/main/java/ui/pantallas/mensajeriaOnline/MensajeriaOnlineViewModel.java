package ui.pantallas.mensajeriaOnline;

import jakarta.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import servicios.ServiciosMensaje;
import ui.pantallas.common.ConstantesPantallas;
import ui.pantallas.mensajeria.MensajeriaState;

import java.util.List;

public class MensajeriaOnlineViewModel {
    private final ServiciosMensaje serviciosMensaje;
    private final ObjectProperty<MensajeriaOnlineState> state;

    private List<MensajeCaja> mensajesCaja;

    @Inject
    public MensajeriaOnlineViewModel(ServiciosMensaje serviciosMensaje) {
        this.serviciosMensaje = serviciosMensaje;
        this.mensajesCaja = List.of();
        state = new SimpleObjectProperty<>(new MensajeriaOnlineState(null, null));
    }
    public ReadOnlyObjectProperty<MensajeriaOnlineState> getState() {
        return state;
    }

    public void getMensajesCajas(String user,String nombreCarpeta,String contrasena) {
        serviciosMensaje.getMensajesFromCaja(user,nombreCarpeta,contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaOnlineState(null, either.getLeft()));
                    else {
                        if (!either.get().isEmpty()) {
                            mensajesCaja = either.get();
                            state.setValue(new MensajeriaOnlineState(mensajesCaja, null));
                        }else {
                            state.setValue(new MensajeriaOnlineState(null, ConstantesPantallas.CARPETA_VACIA));
                        }

                    }
                });
    }
    public void updateMensaje( MensajeCaja mensajeCaja,String contrasena) {
        serviciosMensaje.updateMensajeCompartida(mensajeCaja,contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaOnlineState(null, either.getLeft()));
                    else
                        getMensajesCajas(mensajeCaja.getNombreLogin(),mensajeCaja.getNombreCarpeta(),contrasena);
                });
    }
    public void limpiarEstado() {
        state.set(new MensajeriaOnlineState(null, null));
    }
}
