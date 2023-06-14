package jakarta.rest;

import domain.servicios.ServicesLogin;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import jakarta.security.common.ConstantesSecurity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import modelo.utils.Login;

@Path(ConstantesRest.LOGIN)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestLogin {

    private final ServicesLogin servicesLogin;

    @Inject
    public RestLogin(ServicesLogin servicesLogin) {
        this.servicesLogin = servicesLogin;
    }

    @Context
    private SecurityContext securityContext;

    @GET
    public Response getLogin() {
        if (securityContext.getUserPrincipal() != null) {
            return Response.status(Response.Status.CREATED)
                    .entity(servicesLogin.getReader(securityContext.getUserPrincipal().getName()))
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    @GET
    @Path("/clave")
    public Response getClavePublicaServer() {
        return Response.ok(servicesLogin.getClavePublicaServer()).build();
    }
    @GET
    @Path(ConstantesRest.LOGIN_ALL)
    public Response getUsuarios() {
        return Response.ok(servicesLogin.getAll()).build();
    }
    @POST
    @RolesAllowed({ConstantesSecurity.ADMIN})
    public Response addUsuario(Login login) {
        return Response.status(Response.Status.CREATED)
                .entity(servicesLogin.addUsuario(login))
                .build();
    }
    @GET
    @Path(ConstantesRest.LOGOUT)
    public Response logout() {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    @GET
    @Path(ConstantesRest.CAMBIAR_CONTRASEÑA)
    public Response cambiarContraseña(@QueryParam(ConstantesRest.EMAIL) String email, @QueryParam(ConstantesRest.USER) String user) {
        servicesLogin.cambiarContrasena(email,user);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    @GET
    @Path(ConstantesRest.REENVIAR_CODIGO)
    public Response reenviarCodigo(@QueryParam(ConstantesRest.EMAIL) String email, @QueryParam(ConstantesRest.USER) String user) {
        servicesLogin.reenviar(email,user);
        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
