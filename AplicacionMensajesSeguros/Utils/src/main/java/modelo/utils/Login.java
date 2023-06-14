package modelo.utils;

import lombok.Data;

@Data
public class Login {

    private String nombre;
    private String contrasena;
    private int idReader;

    private String contrasenaEncriptada;

    private String clavePublica;

    private String certificate;

    private String mail;

    private String codigo;

    private String rol;

    public Login() {
    }

    public Login(String nombre, String contrasena) {
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public Login(String nombre, String contrasena, String rol) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Login(String nombre, String contrasena, int idReader, String mail) {
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.idReader = idReader;
        this.mail = mail;
    }
}
