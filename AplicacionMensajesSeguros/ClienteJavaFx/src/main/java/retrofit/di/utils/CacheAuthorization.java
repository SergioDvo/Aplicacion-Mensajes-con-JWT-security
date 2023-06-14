package retrofit.di.utils;

import jakarta.inject.Singleton;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;


@Data
@Singleton
public class CacheAuthorization {

    private String user;
    private String pass;
    private String jwt;
    private PublicKey pk;
    private PrivateKey clientePrivateKey;
}
