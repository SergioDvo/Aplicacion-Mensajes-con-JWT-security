package dao;


import com.google.gson.Gson;
import config.ConstantesConfig;
import errores.ApiError;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

@Log4j2
abstract class DaoGenerics {

    private final Gson gson;

    @Inject
    public DaoGenerics(Gson gson) {
        this.gson = gson;
    }

    public <T> Single<Either<String, T>> safeSingleApiCall(Single<T> call) {
        return call.map(t -> Either.right(t).mapLeft(Object::toString))
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    Either<String, T> error = Either.left(ConstantesConfig.ERROR_DE_COMUNICACIÓN);
                    if (throwable instanceof HttpException httpException) {
                        try (ResponseBody responseBody = Objects.requireNonNull(httpException.response()).errorBody()) {
                            if (Objects.equals(Objects.requireNonNull(responseBody).contentType(),
                                    MediaType.get(ConstantesConfig.APPLICATION_JSON))) {
                                ApiError apierror = gson.fromJson(responseBody.string(), ApiError.class);
                                error = Either.left(apierror.getMessage());
                            } else {
                                error = Either.left(Objects.requireNonNull(httpException.response()).message());
                                if (Objects.requireNonNull(httpException.response()).code() == 401) {
                                    error = Either.left(ConstantesConfig.ERROR_DE_COMUNICACIÓN);
                                }
                            }
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    return error;
                });
    }
    public Single<String> safeSingleAPICallToDelete(Single<Response<Object>> apiCall) {
        return apiCall.map(Response::message)
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    String error = ConstantesConfig.ERROR_DE_COMUNICACIÓN;
                    if (throwable instanceof HttpException httpException) {
                        try (ResponseBody responseBody = Objects.requireNonNull(httpException.response()).errorBody()) {
                            if (Objects.equals(Objects.requireNonNull(responseBody).contentType(),
                                    MediaType.get(ConstantesConfig.APPLICATION_JSON))) {
                                ApiError apierror = gson.fromJson(responseBody.string(), ApiError.class);
                                error = apierror.getMessage();
                            } else {
                                error = Objects.requireNonNull(httpException.response()).message();
                            }
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    return error;
                });
    }
}



