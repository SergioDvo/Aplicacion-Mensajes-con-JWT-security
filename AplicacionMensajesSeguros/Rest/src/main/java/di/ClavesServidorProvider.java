package di;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ClavesServidorProvider {

    @Produces
    @Singleton
    public KeyPair getClaves(){
        Path clavePublicaPath = Paths.get("clavePublica.txt");
        Path clavePrivadaPath = Paths.get("clavePrivada.txt");
        KeyPair keyPair;
        //todo probar
            try {
                byte[] clavePublica = Files.readAllBytes(clavePublicaPath);
                byte[] clavePrivada = Files.readAllBytes(clavePrivadaPath);
                if (clavePublica.length == 0 || clavePrivada.length == 0) {
                    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                    keyGen.initialize(2048);
                    keyPair = keyGen.generateKeyPair();
                    Files.write(clavePublicaPath, keyPair.getPublic().getEncoded());
                    Files.write(clavePrivadaPath, keyPair.getPrivate().getEncoded());
                }else {
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(clavePublica));
                    PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(clavePrivada));
                    keyPair = new KeyPair(publicKey, privateKey);
                }

            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al leer las claves del servidor");
            }
        return keyPair;
    }
}
