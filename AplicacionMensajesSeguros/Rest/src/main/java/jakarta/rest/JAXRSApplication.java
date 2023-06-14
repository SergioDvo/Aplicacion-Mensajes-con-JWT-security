package jakarta.rest;


import jakarta.annotation.security.DeclareRoles;
import jakarta.security.common.ConstantesSecurity;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DeclareRoles({ConstantesSecurity.ADMIN, ConstantesSecurity.USER})
public class JAXRSApplication extends Application {

}
