package jakarta.rest;

import domain.servicios.ServicesMensajesCajas;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.utils.MensajeCaja;

@Path(ConstantesRest.MENSAJES_CAJAS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestMensajesCajas {
    private final ServicesMensajesCajas servicesMensajesCajas;
    @Inject
    public RestMensajesCajas(ServicesMensajesCajas servicesMensajesCajas) {
        this.servicesMensajesCajas = servicesMensajesCajas;
    }
    @GET
    public Response getMensajesCajas(@QueryParam(ConstantesRest.CAJA) String carpeta, @QueryParam(ConstantesRest.NOMBRE) String usuario, @QueryParam(ConstantesRest.CONTRASENA) String contrasena) {
        return Response.ok(servicesMensajesCajas.getMensajesCajas(carpeta, usuario, contrasena)).build();
    }
    @GET
    @Path(ConstantesRest.COMPARTIDA)
    public Response getMensajesCajasCompartida(@QueryParam(ConstantesRest.NOMBRE) String user, @QueryParam(ConstantesRest.CAJA) String caja, @QueryParam(ConstantesRest.CONTRASENA) String contrasena) {
        return Response.ok(servicesMensajesCajas.getMensajesCajasCompartida(user,caja,contrasena)).build();
    }
    @POST
    public Response añadirMensajeCaja(MensajeCaja mensajeCaja) {
        return Response.status(Response.Status.CREATED)
                .entity(servicesMensajesCajas.añadirMensajeCaja(mensajeCaja))
                .build();
    }
    @PUT
    public Response actualizarMensajeCaja(MensajeCaja mensajeCaja) {
        return Response.status(Response.Status.OK)
                .entity(servicesMensajesCajas.updateMensajeCaja(mensajeCaja))
                .build();
    }
    @PUT
    @Path(ConstantesRest.COMPARTIDA)
    public Response actualizarMensajeCajaCompartida(MensajeCaja mensajeCaja) {
        return Response.status(Response.Status.OK)
                .entity(servicesMensajesCajas.updateMensajeCajaCompartida(mensajeCaja))
                .build();
    }
    @DELETE
    @Path("/{id}")
    public Response borrarMensajeCaja(@PathParam("id") int id, @QueryParam("carpeta") String carpeta, @QueryParam(ConstantesRest.CONTRASENA) String contrasena) {
        servicesMensajesCajas.eliminarMensajeCaja(id,carpeta,contrasena);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
}
