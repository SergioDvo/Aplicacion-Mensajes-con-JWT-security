package retrofit.llamadas;

import io.reactivex.rxjava3.core.Single;
import modelo.utils.MensajeCaja;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;

public interface MensajesApi {

    @GET(ConstantesLlamadasRetrofit.MENSAJES)
    Single<List<MensajeCaja>> getAllMensajes(@Query(ConstantesLlamadasRetrofit.CAJA) String caja,@Query(ConstantesLlamadasRetrofit.USER) String user,@Query(ConstantesLlamadasRetrofit.CONTRASENA) String contrasena);
    @GET(ConstantesLlamadasRetrofit.MENSAJES_CAJAS_COMPARTIDA)
    Single<List<MensajeCaja>> getAllMensajesFromCaja(@Query(ConstantesLlamadasRetrofit.USER) String user,@Query(ConstantesLlamadasRetrofit.CAJA) String caja,@Query(ConstantesLlamadasRetrofit.CONTRASENA) String contrasena);
    @POST(ConstantesLlamadasRetrofit.MENSAJES)
    Single<MensajeCaja> añadirMensaje(@Body MensajeCaja mensajeCaja);
    @PUT(ConstantesLlamadasRetrofit.MENSAJES)
    Single<MensajeCaja> updateMensaje(@Body MensajeCaja mensajeCaja);
    @PUT(ConstantesLlamadasRetrofit.MENSAJES_CAJAS_COMPARTIDA)
    Single<MensajeCaja> updateMensajeCompartida(@Body MensajeCaja mensajeCaja);
    @DELETE(ConstantesLlamadasRetrofit.MENSAJES_ID)
    Single<Response<Object>> eliminarMensaje(@Path(ConstantesLlamadasRetrofit.ID) int id, @Query(ConstantesLlamadasRetrofit.CARPETA) String caja,@Query(ConstantesLlamadasRetrofit.CONTRASENA) String contrasena);


}
