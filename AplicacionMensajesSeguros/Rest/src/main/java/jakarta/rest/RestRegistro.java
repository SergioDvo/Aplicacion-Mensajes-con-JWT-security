package jakarta.rest;

import domain.servicios.ServicesLogin;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.utils.Login;

@Path(ConstantesRest.REGISTRO)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestRegistro {
    private final ServicesLogin servicesLogin;

    @Inject
    public RestRegistro(ServicesLogin servicesLogin) {
        this.servicesLogin = servicesLogin;
    }
    @POST
    public Response doRegister(Login login) {
        return Response.status(Response.Status.CREATED)
                .entity(servicesLogin.addUsuario(login))
                .build();
    }
}
