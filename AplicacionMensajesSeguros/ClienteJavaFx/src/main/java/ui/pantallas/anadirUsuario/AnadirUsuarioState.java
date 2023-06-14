package ui.pantallas.anadirUsuario;

import lombok.Data;
import modelo.utils.Carpeta;
import modelo.utils.Login;
import modelo.utils.MensajeCaja;

import java.util.List;
@Data
public class AnadirUsuarioState {
    private final List<Login> listaUsuarios;
    private final String error;
}
