package jakarta.security;

import di.KeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.common.ConstantesSecurity;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.BasicAuthenticationCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;


@ApplicationScoped
public class JWTAuth implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStore identity;

    private final Key provider = KeyProvider.getInstance().getKey();


    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse
            , HttpMessageContext httpMessageContext) {
        CredentialValidationResult c = CredentialValidationResult.INVALID_RESULT;

        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            String[] valores = header.split(" ");

            if (valores[0].equalsIgnoreCase(ConstantesSecurity.BASIC)) {
                c = identity.validate(new BasicAuthenticationCredential(valores[1]));
                if (c.getStatus() == CredentialValidationResult.Status.VALID) {
                    String token = Jwts.builder()
                            .setSubject(ConstantesSecurity.TOKEN)
                            .setIssuer(ConstantesSecurity.SERVIDOR_AUTH)
                            .setExpiration(Date
                                    .from(LocalDateTime.now().plusSeconds(30).atZone(ZoneId.systemDefault())
                                            .toInstant()))
                            .claim(ConstantesSecurity.USUARIO, c.getCallerPrincipal().getName())
                            .claim(ConstantesSecurity.GRUPO, c.getCallerGroups().toArray()[0])
                            .signWith(provider).compact();
                    //añadir al response
                    httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, ConstantesSecurity.BEARER + " " + token);


                }
            } else if (valores[0].equalsIgnoreCase(ConstantesSecurity.BEARER)) {
                //validar token
                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(provider)
                        .build()
                        .parseClaimsJws(valores[1]);

                c = new CredentialValidationResult(claimsJws.getBody().get(ConstantesSecurity.USUARIO).toString(),
                        Set.of(claimsJws.getBody().get(ConstantesSecurity.GRUPO).toString()));

                httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, ConstantesSecurity.BEARER + " " + valores[1]);
            }
        }
        if (!c.getStatus().equals(CredentialValidationResult.Status.VALID)) {
            httpServletRequest.setAttribute(ConstantesSecurity.STATUS, c.getStatus());
            return httpMessageContext.doNothing();
        }
        return httpMessageContext.notifyContainerAboutLogin(c);
    }
}
