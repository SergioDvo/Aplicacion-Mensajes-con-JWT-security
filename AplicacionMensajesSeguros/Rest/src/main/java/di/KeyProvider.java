package di;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

import java.security.Key;

public class KeyProvider {

    private final Key key;
    private static KeyProvider instance;

    private KeyProvider() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
    public static KeyProvider getInstance() {
        if (instance == null) {
            instance = new KeyProvider();
        }
        return instance;
    }
    public Key getKey() {
        return key;
    }

}
