package retrofit.di;

import config.ConstantesConfig;
import jakarta.inject.Inject;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit.di.utils.CacheAuthorization;

import java.io.IOException;

public class    AuthorizationInterceptor implements Interceptor {


    private final CacheAuthorization ca;

    @Inject
    public AuthorizationInterceptor(CacheAuthorization ca) {
        this.ca = ca;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request ;

        if (ca.getJwt() == null) {
            request = original;
        }
        else
        {
            request = original.newBuilder()
                    .header(ConstantesConfig.AUTHORIZATION, ca.getJwt()).build();

        }

        Response response = chain.proceed(request);
        if (response.header(ConstantesConfig.AUTHORIZATION) !=null)
            ca.setJwt(response.header(ConstantesConfig.AUTHORIZATION));


        if (!response.isSuccessful() && response.code() != 403)
        {
            //reintentar
            response.close();
            request = original.newBuilder()
                    .header(ConstantesConfig.AUTHORIZATION, Credentials.basic(ca.getUser(), ca.getPass())).build();
            response = chain.proceed(request);
            if (response.header(ConstantesConfig.AUTHORIZATION) !=null)
                ca.setJwt(response.header(ConstantesConfig.AUTHORIZATION));
        }

        return response;
    }
}
