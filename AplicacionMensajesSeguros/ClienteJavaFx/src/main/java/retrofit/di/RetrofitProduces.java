package retrofit.di;

import com.google.gson.*;
import config.Configuracion;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit.di.utils.CacheAuthorization;
import retrofit.llamadas.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.CookieManager;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.net.CookiePolicy;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitProduces {


    @Produces
    @Singleton
    public Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString())).registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> new JsonPrimitive(localDate.toString())
                ).registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> LocalDateTime.parse(json.getAsJsonPrimitive().getAsString())).registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (localDate, type, jsonSerializationContext) -> new JsonPrimitive(localDate.toString())
                ).create();
    }
    @Produces
    @Singleton
    public Retrofit retrofit(Gson gson, Configuracion config,AuthorizationInterceptor authorizationInterceptor) {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        OkHttpClient clientOK = new OkHttpClient.Builder()
                .readTimeout(Duration.of(10, ChronoUnit.MINUTES))
                .callTimeout(Duration.of(10, ChronoUnit.MINUTES))
                .connectTimeout(Duration.of(10, ChronoUnit.MINUTES))
                .connectionPool(new okhttp3.ConnectionPool(1, 3, java.util.concurrent.TimeUnit.SECONDS))
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(authorizationInterceptor)
                .build();



        return new Retrofit.Builder()
                .baseUrl(config.getUrlBase())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(clientOK)
                .build();

    }

    @Produces
    public MensajesApi getMensajesApi(Retrofit retrofit){
        return retrofit.create(MensajesApi.class);
    }

    @Produces
    public LoginApi getLoginApi(Retrofit retrofit){
        return retrofit.create(LoginApi.class);
    }
    @Produces
    public CajaApi getCajaApi(Retrofit retrofit){
        return retrofit.create(CajaApi.class);
    }


}
