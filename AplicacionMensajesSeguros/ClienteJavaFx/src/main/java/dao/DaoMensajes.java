package dao;

import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Single;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import retrofit.llamadas.CajaApi;
import retrofit.llamadas.MensajesApi;


import java.util.List;

public class DaoMensajes extends DaoGenerics{
    private final MensajesApi mensajesApi;
    private final CajaApi cajaApi;

    @Inject
    public DaoMensajes(MensajesApi mensajesApi,CajaApi cajaApi ,Gson gson) {
        super(gson);
        this.mensajesApi = mensajesApi;
        this.cajaApi = cajaApi;
    }
    public Single<Either<String, List<MensajeCaja>>> getAllMensajes(String caja,String user,String contrasena){
        return safeSingleApiCall(mensajesApi.getAllMensajes(caja,user,contrasena));
    }
    public Single<Either<String, List<MensajeCaja>>> getAllMensajesFromCaja(String user,String caja,String contrasena){
        return safeSingleApiCall(mensajesApi.getAllMensajesFromCaja(user,caja,contrasena));
    }
    public Single<Either<String, MensajeCaja>> añadirMensaje(MensajeCaja mensajeCaja){
        return safeSingleApiCall(mensajesApi.añadirMensaje(mensajeCaja));
    }
    public Single<Either<String, MensajeCaja>> updateMensaje(MensajeCaja mensajeCaja){
        return safeSingleApiCall(mensajesApi.updateMensaje(mensajeCaja));
    }
    public Single<Either<String, MensajeCaja>> updateMensajeCompartida(MensajeCaja mensajeCaja){
        return safeSingleApiCall(mensajesApi.updateMensajeCompartida(mensajeCaja));
    }
    public Single<String> eliminarMensaje(MensajeCaja mensajeCaja,String contrasena){
        return safeSingleAPICallToDelete(mensajesApi.eliminarMensaje(mensajeCaja.getId(),mensajeCaja.getNombreCarpeta(),contrasena));
    }
    public Single<Either<String,List<String>>> getAllCajas(String usuario){
        return safeSingleApiCall(cajaApi.getAllCajas(usuario));
    }
    public Single<Either<String, Carpeta>> añadirCaja(Carpeta carpeta){
        return safeSingleApiCall(cajaApi.añadirCaja(carpeta));
    }
    public Single<Either<String, Carpeta>> updateCarpeta(Carpeta carpeta,String contrasena){
        return safeSingleApiCall(cajaApi.updateCaja(carpeta,contrasena));
    }
}
