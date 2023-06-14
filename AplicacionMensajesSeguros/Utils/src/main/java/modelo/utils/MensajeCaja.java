package modelo.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensajeCaja {
    private int id;
    private String mensaje;
    private String nombreCarpeta;
    private String nombreLogin;

    public MensajeCaja(){

    }

    public MensajeCaja(String mensaje, String nombreCarpeta, String nombreLogin) {
        this.mensaje = mensaje;
        this.nombreCarpeta = nombreCarpeta;
        this.nombreLogin = nombreLogin;
    }
}
