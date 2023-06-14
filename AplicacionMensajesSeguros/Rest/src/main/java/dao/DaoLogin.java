package dao;

import JDBC.DBConnectionPool;
import config.MailConfig;
import dao.constant.SQLQueries;
import domain.errores.ConnectionDBException;
import domain.errores.DataFailureException;
import domain.errores.LoginError;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.rest.common.ConstantesRest;
import jakarta.security.common.ConstantesSecurity;
import modelo.utils.Login;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import utilss.GenerateCode;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DaoLogin {
    public static final String MENSAJE_CORREO_2 = "<html>Dale a este enlace para cambiar la contraseña <a href=\"http://localhost:8080/Rest-1.0-SNAPSHOT/cambiarContrasena.html?codigo=";
    private final DBConnectionPool pool;

    private final MailConfig mailConfig;

    @Inject
    public DaoLogin(DBConnectionPool pool, MailConfig mailConfig) {
        this.pool = pool;
        this.mailConfig = mailConfig;
    }
    public List<Login> getUsuarios() {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(pool.getDataSource());
            return jdbcTemplate.query(SQLQueries.GET_ALL_LOGIN, new BeanPropertyRowMapper<>(Login.class));
        } catch (Exception e) {
            throw new DataFailureException(ConstantesRest.ERROR_EN_EL_SERVIDOR);
        }
    }
    public Login addUsuario(Login login) {
        try (Connection con = pool.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(SQLQueries.INSERT_LOGIN)) {
            preparedStatement.setString(1, login.getNombre());
            preparedStatement.setString(2, login.getContrasena());
            preparedStatement.setString(3, login.getRol());
            preparedStatement.executeUpdate();
            return login;
        } catch (SQLException e) {
            throw new DataFailureException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConnectionDBException(e.getMessage());
        }
    }

    public Login getLogin(String user) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            List<Login> loginList = jtm.query(SQLQueries.SELECT_LOGIN_USER_VERIFIED, BeanPropertyRowMapper.newInstance(Login.class), user);
            if (!loginList.isEmpty()) {
                return loginList.get(0);
            }
            throw new LoginError(ConstantesRest.CONTRASEÑA_O_USUARIO_INCORRECTO);
        } catch (Exception e) {
            throw new ConnectionDBException(ConstantesRest.ERROR_EN_EL_SERVIDOR);
        }
    }




    public Login addLogin(Login login) {
        TransactionDefinition txDef = new DefaultTransactionDefinition();
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(pool.getDataSource());
        TransactionStatus txStatus = transactionManager.getTransaction(txDef);
        KeyHolder id = new GeneratedKeyHolder();
        try {
            String codigoActivacion = GenerateCode.randomBytes();
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            int rowsaffected = jtm.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQLQueries.INSERT_READER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, login.getNombre());
                ps.setDate(2, Date.valueOf(LocalDate.now()));
                return ps;
            }, id);
            long generatedKey = id.getKey().longValue();
            int activacion = 0;
            rowsaffected += jtm.update(SQLQueries.INSERT_LOGIN_USER, login.getNombre(), login.getContrasena(), generatedKey, codigoActivacion, LocalDateTime.now(), activacion, login.getMail(), ConstantesSecurity.USER);
            if (rowsaffected >= 2) {
                try {
                    generarYenviarEmail(login.getMail(), ConstantesRest.MENSAJE_CORREO + codigoActivacion + ConstantesRest.AQUI_A, ConstantesRest.VERIFICACION);
                    transactionManager.commit(txStatus);
                } catch (MessagingException e) {
                    throw new DataFailureException(ConstantesRest.ERROR_AL_ENVIAR_EL_EMAIL_DE_ACTIVACION);
                }
            }
            return login;
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw new ConnectionDBException(e.getMessage());
        }

    }

    private void generarYenviarEmail(String to, String msg, String subject) throws MessagingException {
        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage generateMailMessage;

        // Step1

        mailServerProperties = System.getProperties();
        mailServerProperties.put(mailConfig.getPort(), Integer.parseInt(mailConfig.getPortNumber()));
        mailServerProperties.put(mailConfig.getMailSmtpAuth(), ConstantesRest.TRUE);
        mailServerProperties.put(mailConfig.getSslTrust(), mailConfig.getSmtpGmailCom());
        mailServerProperties.put(mailConfig.getStarttlsEnable(), ConstantesRest.TRUE);

        // Step2

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        generateMailMessage.setSubject(subject);
        String emailBody = msg;
        generateMailMessage.setContent(emailBody, ConstantesRest.TEXT_HTML);

        Transport transport = getMailSession.getTransport(ConstantesRest.SMTP);

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        transport.connect(mailConfig.getSmtpGmailCom(),
                ConstantesRest.ALUMNOSDAMQUEVEDO_GMAIL_COM,
                ConstantesRest.AYUAKLCKGXBBOOPH);
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
    }

    public void activar(String codigo) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            int rowsaffected = jtm.update(SQLQueries.ACTIVAR_LOGIN, codigo, LocalDateTime.now().plusMinutes(5));
            if (rowsaffected == 0) {
                throw new DataFailureException(ConstantesRest.NO_SE_HA_PODIDO_VERIFICAR_EL_USUARIO);
            }
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }

    public void reenviarCorreo(String mail, String user) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            String codigoActivacion = GenerateCode.randomBytes();
            int rowsAffected = jtm.update(SQLQueries.UPDATE_LOGIN_REENVIO_MAIL, LocalDateTime.now(), codigoActivacion, user, mail);
            if (rowsAffected == 1) {
                generarYenviarEmail(mail, ConstantesRest.MENSAJE_CORREO + codigoActivacion + ConstantesRest.AQUI_A, ConstantesRest.REENVIO_DE_VERIFICACION);
            } else {
                throw new LoginError(ConstantesRest.ESE_USUARIO_NO_EXISTE_O_NO_TIENE_ESE_MAIL);
            }
        } catch (Exception e) {
            throw new ConnectionDBException(ConstantesRest.ERROR_EN_LA_BASE_DE_DATOS);
        }
    }

    public void cambiarContrasena(String mail, String user) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            List<Login> loginList = jtm.query(SQLQueries.SELECT_LOGIN_USER, BeanPropertyRowMapper.newInstance(Login.class), user,mail);
            if (!loginList.isEmpty()) {
                generarYenviarEmail(mail, MENSAJE_CORREO_2 +loginList.get(0).getCodigo()+ ConstantesRest.AQUI_A, ConstantesRest.REENVIO_DE_VERIFICACION);
            }else {
                throw new LoginError(ConstantesRest.ESE_USUARIO_NO_EXISTE_O_NO_TIENE_ESE_MAIL);
            }
        } catch (Exception e) {
            throw new ConnectionDBException(ConstantesRest.ERROR_EN_LA_BASE_DE_DATOS);
        }
    }
    public boolean updateContrasena(String contrasena, String codigo) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            int rowsAffected = jtm.update(SQLQueries.UPDATE_LOGIN_CONTRASENA, contrasena, codigo);
            if (rowsAffected == 1) {
                return true;
            } else {
                Logger.getLogger(DaoLogin.class.getName()).log(Level.SEVERE, null, ConstantesRest.CODIGO_ERRONEO);
                return false;
            }
        } catch (Exception e) {
            Logger.getLogger(DaoLogin.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

}

