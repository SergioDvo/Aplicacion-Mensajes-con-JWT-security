package jakarta.security;

import domain.servicios.ServicesLogin;
import jakarta.inject.Inject;
import jakarta.security.common.ConstantesSecurity;
import jakarta.security.enterprise.credential.BasicAuthenticationCredential;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import modelo.utils.Login;

import java.util.Collections;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;


public class IdentityStore implements jakarta.security.enterprise.identitystore.IdentityStore {

    private final ServicesLogin servicesLogin;

    @Override
    public int priority() {
        return 10;
    }


    @Inject
    public IdentityStore(ServicesLogin servicesLogin) {
        this.servicesLogin = servicesLogin;
    }


    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof BasicAuthenticationCredential user) {
            Login login = servicesLogin.doLoginValidation(user.getCaller(), user.getPasswordAsString());
            return switch (login.getRol()) {
                case ConstantesSecurity.ADMIN ->
                        new CredentialValidationResult(login.getNombre(), Collections.singleton(ConstantesSecurity.ADMIN));
                case ConstantesSecurity.USER ->
                        new CredentialValidationResult(login.getNombre(), Collections.singleton(ConstantesSecurity.USER));
                default -> INVALID_RESULT;
            };
        } else {
            return INVALID_RESULT;
        }
    }
}