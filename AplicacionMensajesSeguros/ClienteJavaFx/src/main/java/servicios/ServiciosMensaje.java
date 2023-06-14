package servicios;

import dao.DaoMensajes;
import io.reactivex.rxjava3.core.Single;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import org.example.security.Encriptacion;

import java.util.List;
import java.util.stream.Collectors;

public class ServiciosMensaje {

    private final DaoMensajes daoMensajes;

    private final Encriptacion encriptacion;

    @Inject
    public ServiciosMensaje(DaoMensajes daoMensajes, Encriptacion encriptacion) {
        this.daoMensajes = daoMensajes;
        this.encriptacion = encriptacion;
    }

    public Single<Either<String, List<MensajeCaja>>> getMensajes(String caja, String user,String contrasena){
            return daoMensajes.getAllMensajes(caja, user, contrasena)
                    .map(either -> either.map(mensaje -> {
                            mensaje.forEach(m -> m.setMensaje(encriptacion.desencriptar(m.getMensaje(), contrasena)));
                            return mensaje;
                    }));

    }
    public Single<Either<String, List<MensajeCaja>>> getMensajesFromCaja(String user,String caja,String contrasena){
        return daoMensajes.getAllMensajesFromCaja(user,caja,contrasena)
                .map(either -> either.mapLeft(error -> error)
                        .map(mensajes -> mensajes.stream()
                                .map(mensaje -> new MensajeCaja(mensaje.getId(),encriptacion.desencriptar(mensaje.getMensaje(),contrasena),mensaje.getNombreCarpeta(),mensaje.getNombreLogin()))
                                .collect(Collectors.toList())));
    }
    public Single<Either<String, MensajeCaja>> añadirMensaje(MensajeCaja mensajeCaja,String contrasena){
        String mensajeEncriptado = encriptacion.encriptar(mensajeCaja.getMensaje(),contrasena);
        mensajeCaja.setMensaje(mensajeEncriptado);
        return daoMensajes.añadirMensaje(mensajeCaja);
    }
    public Single<Either<String, MensajeCaja>> updateMensaje(MensajeCaja mensajeCaja,String contrasena){
        String mensajeEncriptado = encriptacion.encriptar(mensajeCaja.getMensaje(),contrasena);
        mensajeCaja.setMensaje(mensajeEncriptado);
        return daoMensajes.updateMensaje(mensajeCaja);
    }
    public Single<Either<String, MensajeCaja>> updateMensajeCompartida(MensajeCaja mensajeCaja,String contrasena){
        String mensajeEncriptado = encriptacion.encriptar(mensajeCaja.getMensaje(),contrasena);
        mensajeCaja.setMensaje(mensajeEncriptado);
        return daoMensajes.updateMensajeCompartida(mensajeCaja);
    }
    public Single<String> eliminarMensaje(MensajeCaja mensajeCaja,String contrasena){
        return daoMensajes.eliminarMensaje(mensajeCaja,contrasena);
    }
    public Single<Either<String,List<String>>> getCajas(String usuario){
        return daoMensajes.getAllCajas(usuario);
    }
    public Single<Either<String, Carpeta>> añadirCarpeta(Carpeta carpeta){
        return daoMensajes.añadirCaja(carpeta);
    }
    public Single<Either<String, Carpeta>> cambiarContrasena(Carpeta carpeta,String contrasena){
        return daoMensajes.updateCarpeta(carpeta,contrasena);
    }
}
