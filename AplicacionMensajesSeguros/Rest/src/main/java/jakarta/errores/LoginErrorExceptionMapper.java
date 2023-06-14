package jakarta.errores;

import domain.errores.ConnectionDBException;
import domain.errores.DataFailureException;
import domain.errores.LoginError;
import errores.ApiError;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class LoginErrorExceptionMapper implements ExceptionMapper<LoginError> {

    public Response toResponse(LoginError exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiError.builder()
                        .message(exception.getMessage())
                        .fecha(LocalDateTime.now())
                        .build())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
