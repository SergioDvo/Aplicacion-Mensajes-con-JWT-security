package domain.servicios;

import dao.DaoLogin;
import di.ClavesServidorProvider;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import domain.errores.LoginError;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import modelo.utils.Login;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.example.security.Encriptacion;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class ServicesLogin {

    private final DaoLogin daoLogin;
    private Pbkdf2PasswordHash passwordHash;
    private final KeyPair keyPair;
    private final Encriptacion encriptacion;

    @Inject
    public ServicesLogin(DaoLogin daoLogin, Pbkdf2PasswordHash passwordHash, ClavesServidorProvider keyPair, Encriptacion encriptacion) {
        this.daoLogin = daoLogin;
        this.passwordHash = passwordHash;
        this.keyPair = keyPair.getClaves();
        this.encriptacion = encriptacion;
    }
    public List<Login> getAll() {
        return daoLogin.getUsuarios();
    }
    public String getClavePublicaServer(){
        //coger el produces del generador de contraseñas y devolver la publica
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
    public Login addUsuario(Login login){
        login.setContrasena(passwordHash.generate(login.getContrasena().toCharArray()));
        //coger las dos contraseñas la publicaencriptada y la contraseña encriptada con la publica del servidor
        byte [] publicKeyBytes;
        try {
            Cipher cifrador = Cipher.getInstance("RSA");
            cifrador.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            //getDecoder o getUrlDecoder
            byte[] contraseñaEncriptada = Base64.getDecoder().decode(login.getContrasenaEncriptada());
            byte[] contraseñaDesencriptada = cifrador.doFinal(contraseñaEncriptada);
            String clavePublica = encriptacion.desencriptar(login.getClavePublica(),new String(contraseñaDesencriptada));
            publicKeyBytes = Base64.getDecoder().decode(clavePublica);
        } catch (NoSuchPaddingException |InvalidKeyException|IllegalBlockSizeException|BadPaddingException| NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        //todo fechas

        //crear el certificado
        X509EncodedKeySpec clavePublicaSpec = new X509EncodedKeySpec(publicKeyBytes);


        X509v3CertificateBuilder certGen;
        try {
            //hacer el certificado con la publica del usuario y firmado por la privada del servidor
            X500Name owner = new X500Name("CN="+login.getNombre());
            X500Name issuer = new X500Name("CN=Servidor");
            certGen = new JcaX509v3CertificateBuilder(
                    issuer,
                    BigInteger.valueOf(1),
                    Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)), //not valid before
                    Date.from(LocalDate.now().plus(1, ChronoUnit.YEARS).atStartOfDay().toInstant(ZoneOffset.UTC)),
                    owner,
                    KeyFactory.getInstance("RSA").generatePublic(clavePublicaSpec)
                    );

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(keyPair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certGen.build(signer));
            login.setCertificate(Base64.getEncoder().encodeToString(cert.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return daoLogin.addUsuario(login);
    }
    public Login getReader(String user) {
        return daoLogin.getLogin(user);
    }
    public Login doLoginValidation(String user, String password) {
        Login login = daoLogin.getLogin(user);
        if (login != null) {
            try {
                boolean verificado = passwordHash.verify(password.toCharArray(), login.getContrasena());
                if (verificado) {
                    return login;
                } else {
                    throw new LoginError(ConstantesRest.CONTRASEÑA_INCORRECTA);
                }
            } catch (Exception e) {
                throw new LoginError(ConstantesRest.USUARIO_NO_EXISTE_O_NO_ESTAS_VERIFICADO);
            }

        }
        throw new LoginError(ConstantesRest.USUARIO_NO_EXISTE_O_NO_ESTAS_VERIFICADO);
    }
    public void reenviar(String email,String user) {
        daoLogin.reenviarCorreo(email,user);
    }
    public void cambiarContrasena(String email, String user) {
        daoLogin.cambiarContrasena(email,user);
    }
    public boolean updateContrasena(String password,String codigo) {
        String passwordHashed = passwordHash.generate(password.toCharArray());
        return daoLogin.updateContrasena(passwordHashed,codigo);
    }
}
