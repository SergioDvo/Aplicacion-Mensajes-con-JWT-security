package modelo.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Carpeta {
    private String nombre;
    private String nombreLogin;
    private String contrasena;
    private String estado;

    public Carpeta(){

    }

    public Carpeta(String nombre, String nombreLogin, String contrasena) {
        this.nombre = nombre;
        this.nombreLogin = nombreLogin;
        this.contrasena = contrasena;
    }
}
