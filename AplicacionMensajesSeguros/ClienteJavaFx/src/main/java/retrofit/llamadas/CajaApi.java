package retrofit.llamadas;

import io.reactivex.rxjava3.core.Single;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;

public interface CajaApi {

    @GET(ConstantesLlamadasRetrofit.CAJAS)
    Single<List<String>> getAllCajas(@Query(ConstantesLlamadasRetrofit.USER) String usuario);
    @POST(ConstantesLlamadasRetrofit.CAJAS)
    Single<Carpeta> añadirCaja(@Body Carpeta carpeta);
    @PUT(ConstantesLlamadasRetrofit.CAJAS)
    Single<Carpeta> updateCaja(@Body Carpeta carpeta,@Query(ConstantesLlamadasRetrofit.CONTRASENA) String contrasena);


}
