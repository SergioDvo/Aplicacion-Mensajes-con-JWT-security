package ui.pantallas.mensajeriaOnline;

import lombok.Data;
import modelo.utils.MensajeCaja;

import java.util.List;

@Data
public class MensajeriaOnlineState {
    private final List<MensajeCaja> mensajesCajas;
    private final String error;
}
