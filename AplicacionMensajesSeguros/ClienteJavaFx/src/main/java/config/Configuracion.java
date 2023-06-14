package config;


import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Properties;

@Getter
@Log4j2
@Singleton
public class Configuracion {
    public Configuracion() {

        try {
            Properties p = new Properties();
            p.load(getClass().getClassLoader().getResourceAsStream(ConstantesConfig.CONFIG_PROPERTIES));
            this.urlBase = p.getProperty(ConstantesConfig.URL_BASE);
            this.apikey = p.getProperty(ConstantesConfig.URL_APIKEY);
            this.hash = p.getProperty(ConstantesConfig.HASH);
            this.ts = p.getProperty(ConstantesConfig.TS);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    private String urlBase;
    private String apikey;
    private String hash;
    private String ts;


}