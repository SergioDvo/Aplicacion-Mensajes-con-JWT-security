package ui.pantallas.mensajeria;

import lombok.Data;
import modelo.utils.MensajeCaja;

import java.util.List;
@Data
public class MensajeriaState {
    private final List<MensajeCaja> mensajesCajas;
    private final String error;
    private final List<String> carpetas;
    private final boolean isOk;
}
