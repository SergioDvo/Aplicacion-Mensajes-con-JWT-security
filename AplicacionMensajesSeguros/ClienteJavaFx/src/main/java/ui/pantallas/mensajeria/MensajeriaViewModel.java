package ui.pantallas.mensajeria;

import jakarta.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import servicios.ServiciosMensaje;
import ui.pantallas.common.ConstantesPantallas;

import java.util.List;

public class MensajeriaViewModel {
    private final ServiciosMensaje serviciosMensaje;
    private final ObjectProperty<MensajeriaState> state;

    private List<MensajeCaja> mensajesCaja;
    private List<Carpeta> carpetas;

    @Inject
    public MensajeriaViewModel(ServiciosMensaje serviciosMensaje) {
        this.serviciosMensaje = serviciosMensaje;
        this.mensajesCaja = List.of();
        state = new SimpleObjectProperty<>(new MensajeriaState(null, null, null, false));
    }

    public ReadOnlyObjectProperty<MensajeriaState> getState() {
        return state;
    }

    public void cargarCarpertas(String nombre) {
        serviciosMensaje.getCajas(nombre)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaState(null, either.getLeft(), null, false));
                    else if (either.get().isEmpty())
                        state.setValue(new MensajeriaState(null, ConstantesPantallas.CARPETA_VACIA_O_CREDENCIALS_INCORRECTOS, null, false));
                    else {
                        state.setValue(new MensajeriaState(null, null, either.get(), false));

                    }
                });
    }

    public void getMensajesCajas(String nombreCarpeta, String user, String contrasena) {
        serviciosMensaje.getMensajes(nombreCarpeta, user, contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaState(null, either.getLeft(), null, false));
                    else {
                        if (!either.get().isEmpty()) {
                            state.setValue(new MensajeriaState(either.get(), null, null, true));
                            mensajesCaja = either.get();
                        }
                    }
                });
    }

    public void añadirMensaje(MensajeCaja mensajeCaja, String contrasena) {
        serviciosMensaje.añadirMensaje(mensajeCaja, contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaState(null, either.getLeft(), null, false));
                    else {
                        getMensajesCajas(mensajeCaja.getNombreCarpeta(), mensajeCaja.getNombreLogin(), contrasena);
                    }
                });
    }

    public void updateMensaje(MensajeCaja mensajeCaja, String contrasena) {
        serviciosMensaje.updateMensaje(mensajeCaja, contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaState(null, either.getLeft(), null, false));
                    else
                        getMensajesCajas(mensajeCaja.getNombreCarpeta(), mensajeCaja.getNombreLogin(), contrasena);
                });
    }

    public void eliminarMensaje(MensajeCaja mensajeCaja, String contrasena) {
        serviciosMensaje.eliminarMensaje(mensajeCaja, contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(string -> {
                    if (string.equals(ConstantesPantallas.NO_CONTENT)) {
                        mensajesCaja.remove(mensajeCaja);
                        state.setValue(new MensajeriaState(mensajesCaja, null, null, false));
                    } else {
                        state.setValue(new MensajeriaState(null, string, null, false));
                    }
                });
    }

    public void añadirCarpeta(Carpeta carpeta) {
        serviciosMensaje.añadirCarpeta(carpeta)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaState(null, either.getLeft(), null, false));
                    else
                        cargarCarpertas(carpeta.getNombreLogin());

                });
    }

    public void cambiarContrasena(Carpeta carpeta, String contrasena) {
        serviciosMensaje.cambiarContrasena(carpeta, contrasena)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(either -> {
                    if (either.isLeft())
                        state.setValue(new MensajeriaState(null, either.getLeft(), null, false));
                    else
                        state.setValue(new MensajeriaState(null, null, null, true));
                });
    }

    public void limpiarEstado() {
        state.set(new MensajeriaState(null, null, null, false));
    }
}
