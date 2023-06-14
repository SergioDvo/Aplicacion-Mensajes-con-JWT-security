package jakarta.rest;

import domain.servicios.ServicesMensajesCajas;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.utils.Carpeta;

@Path(ConstantesRest.CAJAS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestCajas {
    private final ServicesMensajesCajas servicesMensajesCajas;
    @Inject
    public RestCajas(ServicesMensajesCajas servicesMensajesCajas) {
        this.servicesMensajesCajas = servicesMensajesCajas;
    }
    @GET
    public Response getCajasFromUser(@QueryParam(ConstantesRest.NOMBRE) String usuario) {
        return Response.ok(servicesMensajesCajas.getCajasFromUser(usuario)).build();
    }
    @POST
    public Response añadirCaja(Carpeta carpeta) {
        return Response.status(Response.Status.CREATED)
                .entity(servicesMensajesCajas.añadirCaja(carpeta))
                .build();
    }
    @PUT
    public Response updateCaja(Carpeta carpeta,@QueryParam(ConstantesRest.CONTRASENA) String contrasena) {
        return Response.status(Response.Status.OK)
                .entity(servicesMensajesCajas.updateCaja(carpeta,contrasena))
                .build();
    }
    
}
