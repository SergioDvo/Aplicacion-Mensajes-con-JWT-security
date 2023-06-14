package dao;

import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Single;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import modelo.utils.Login;
import retrofit.llamadas.LoginApi;

import java.util.List;


public class DaoLogin extends DaoGenerics {

    private final LoginApi loginApi;

    @Inject
    public DaoLogin(LoginApi loginApi, Gson gson) {
        super(gson);
        this.loginApi = loginApi;
    }
    public Single<Either<String, List<Login>>> getUsuarios() {
        return safeSingleApiCall(loginApi.getUsuarios());
    }
    public Single<Either<String, Login>> añadirUsuario(Login login) {
        return safeSingleApiCall(loginApi.añadirUsuario(login));
    }
    public Single<Either<String, Login>> doLogin(String credentials){
        return safeSingleApiCall(loginApi.doLogin(credentials));
    }
    public Single<String> logout(){
        return safeSingleAPICallToDelete(loginApi.logout());
    }
    public Single<String> reenviarCodigo(String email, String user){
        return safeSingleAPICallToDelete(loginApi.reenviarCodigo(email, user));
    }
    public Single<Either<String, Login>> doRegister(Login login){
        return safeSingleApiCall(loginApi.doRegister(login));
    }
    public Single<String> cambiarContraseña(String email, String user){
        return safeSingleAPICallToDelete(loginApi.cambiarContraseña(email, user));
    }
    public Single<Either<String,List<String>>> getClavePublicaServer(){
        return safeSingleApiCall(loginApi.getClavePublicaServer());
    }

}
