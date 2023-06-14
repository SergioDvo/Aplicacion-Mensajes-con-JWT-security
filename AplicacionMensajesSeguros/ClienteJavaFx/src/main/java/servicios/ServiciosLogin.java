package servicios;

import dao.DaoLogin;
import io.reactivex.rxjava3.core.Single;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import modelo.utils.Login;
import okhttp3.Credentials;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.security.Encriptacion;
import retrofit.di.utils.CacheAuthorization;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

public class ServiciosLogin {
    private final DaoLogin daoLogin;
    private CacheAuthorization ca;
    private final Encriptacion encriptacion;

    @Inject
    public ServiciosLogin(DaoLogin daoLogin, CacheAuthorization ca, Encriptacion encriptacion) {
        this.encriptacion = encriptacion;
        this.daoLogin = daoLogin;
        this.ca = ca;

    }
    public Single<Either<String, List<Login>>> getUsuarios() {
        return daoLogin.getUsuarios();
    }
    public Single<Either<String, Login>> añadirUsuario(Login login) {
        return daoLogin.añadirUsuario(login);
    }

    public Single<Either<String, Login>> doLogin(String user, String password){
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(user+"keystore.pfx"), password.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //cogemos la clave del ks
        PrivateKey privateKey;
        try {
            privateKey = (PrivateKey) ks.getKey("privada", password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //genera un string aleatorio
        String aleatorio = new SecureRandom().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        //firmar el aleatorio con la clave privada
        String stringFirmado;
        try{
            Signature firma = Signature.getInstance("SHA256withRSA");
            firma.initSign(privateKey);
            firma.update(aleatorio.getBytes());
            byte [] firmaBytes = firma.sign();
            //codificar la firma en base64
            stringFirmado = Base64.getUrlEncoder().encodeToString(firmaBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //hay que enviar el stringAleatorio la firma y el nombre de usuario
        String credentials = Credentials.basic(user, password);
        ca.setUser(user);
        ca.setPass(password);
        return daoLogin.doLogin(credentials);
    }
    public Single<Either<String, Login>> doRegister(Login login,PublicKey publicKeyServer){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            ca.setClientePrivateKey(privateKey);
            //codifico la cable publica con x509
            X509EncodedKeySpec clavePublicax509Spec = new X509EncodedKeySpec(publicKey.getEncoded());
            //Se obtiene clave publica delX509 en code64 para pode rluego mandarla con el usuario
            String clavePublicaCodeada = Base64.getUrlEncoder().encodeToString(clavePublicax509Spec.getEncoded());
            //Encriptar la clave con la contraseña aletaria anterior y se la metes al user
            String clavePublicaEncriptada = encriptacion.encriptar(clavePublicaCodeada,login.getContrasena());
            login.setClavePublica(clavePublicaEncriptada);
            //Encriptas la clave anterior ("aleatoria") con la clave publica del servidor y la metes al user tambien
            Cipher cifrador = Cipher.getInstance("RSA");
            cifrador.init(Cipher.ENCRYPT_MODE, publicKeyServer);
            login.setContrasenaEncriptada(Base64.getUrlEncoder().encodeToString(cifrador.doFinal(login.getContrasena().getBytes())));
            //envias para servidor para crear certificado y despues se guarda en base de datos y se devuelve guardandolo en una keystore en local
            return daoLogin.añadirUsuario(login);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException  | BadPaddingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public String guardarKeysCliente(Login login,PublicKey publicKeyServer){

        //coger el certificado del usuario
        X509Certificate certificadoRecibido;
        try {
            byte[] certificadoDecodificado = Base64.getUrlDecoder().decode(login.getCertificate());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificadoRecibido = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(certificadoDecodificado));
        } catch (CertificateException e) {
            e.printStackTrace();
            return "Error al decodificar el certificado";
        }

        //se guarda en una keystore
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(null, null);
            ks.setCertificateEntry("certificado", certificadoRecibido);
            ks.setKeyEntry("privada", ca.getClientePrivateKey(), login.getContrasena().toCharArray(), new Certificate[]{certificadoRecibido});
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error al guardar el certificado en el keystore";
        }

        //se guarda en un fichero
        try {
            FileOutputStream fos = new FileOutputStream(login.getNombre()+"keystore.pfx");
            ks.store(fos, login.getContrasena().toCharArray());
            fos.close();
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
            return "Error al guardar el keystore en el fichero";
        }
        return "Se ha registrado correctamente";

    }
    public Single<String> logout(){
        ca.setUser(null);
        ca.setPass(null);
        ca.setJwt(null);
        return daoLogin.logout();
    }
    public Single<String> reenviarCodigo(String email, String user){
        return daoLogin.reenviarCodigo(email,  user);
    }
    public Single<String> cambiarContraseña(String email, String user){
        return daoLogin.cambiarContraseña(email,  user);
    }
    public Single<Either<String, List<String>>> getClavePublicaServer(){
        return daoLogin.getClavePublicaServer();
    }

}
