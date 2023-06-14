package config;

import jakarta.inject.Singleton;
import lombok.Getter;

import java.util.Properties;
@Getter
@Singleton
public class MailConfig {

    private String port;

    private String portNumber;
    private String sslTrust;
    private String mailSmtpAuth;
    private String starttlsEnable;
    private String smtpGmailCom;

    public MailConfig() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream(CostantesConfig.MAIL_CONFIG_YAML));
            this.port = properties.getProperty(CostantesConfig.PORT);
            this.portNumber = properties.getProperty(CostantesConfig.PORT_NUMBER);
            this.mailSmtpAuth = properties.getProperty(CostantesConfig.MAIL_SMTP_AUTH);
            this.sslTrust = properties.getProperty(CostantesConfig.SSL_TRUST);
            this.smtpGmailCom = properties.getProperty(CostantesConfig.SMTP_GMAIL_COM);
            this.starttlsEnable = properties.getProperty(CostantesConfig.STARTTLS_ENABLE);
        } catch (Exception ignored) {
        }
    }
}
