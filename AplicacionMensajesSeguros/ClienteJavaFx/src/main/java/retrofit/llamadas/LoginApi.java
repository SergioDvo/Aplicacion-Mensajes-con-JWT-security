package retrofit.llamadas;

import io.reactivex.rxjava3.core.Single;
import modelo.utils.Login;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;

public interface LoginApi {

    @GET(ConstantesLlamadasRetrofit.LOGIN_ALL)
    Single<List<Login>> getUsuarios();
    @POST(ConstantesLlamadasRetrofit.LOGIN)
    Single<Login> añadirUsuario(@Body Login login);
    @GET(ConstantesLlamadasRetrofit.LOGIN)
    Single<Login> doLogin (@Header(ConstantesLlamadasRetrofit.AUTHORIZATION) String credentials);
    @GET(ConstantesLlamadasRetrofit.LOGIN_CLAVE)
    Single<List<String>> getClavePublicaServer();
    @GET(ConstantesLlamadasRetrofit.LOGOUT)
    Single<Response<Object>> logout();
    @GET(ConstantesLlamadasRetrofit.LOGIN_REENVIAR_CORREO)
    Single<Response<Object>> reenviarCodigo(@Query(ConstantesLlamadasRetrofit.EMAIL) String email, @Query(ConstantesLlamadasRetrofit.USER) String user);
    @POST(ConstantesLlamadasRetrofit.REGISTER)
    Single<Login> doRegister(@Body Login login);
    @GET(ConstantesLlamadasRetrofit.CAMBIAR_CONTRASEÑA)
    Single<Response<Object>> cambiarContraseña(@Query(ConstantesLlamadasRetrofit.EMAIL) String email, @Query(ConstantesLlamadasRetrofit.USER) String user);
}
