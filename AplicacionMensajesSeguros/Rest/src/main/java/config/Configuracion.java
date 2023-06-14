package config;


import jakarta.inject.Singleton;
import lombok.Getter;


import java.util.Properties;

@Getter
@Singleton
public class Configuracion {
    private String ruta;
    private String user;
    private String password;
    private String driver;

    private String secretKey;

    private String maximasLlamadas;

    private String tiempoMaximoLlamadas;

    private String tiempoLimpiarLlamadas;

    public Configuracion() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream(CostantesConfig.CONFIG_YAML));
            this.ruta = properties.getProperty(CostantesConfig.RUTA);
            this.password = properties.getProperty(CostantesConfig.PASSWORD);
            this.user = properties.getProperty(CostantesConfig.USER);
            this.driver = properties.getProperty(CostantesConfig.DRIVER);
            this.secretKey = properties.getProperty(CostantesConfig.SECRET_KEY);
            this.maximasLlamadas = properties.getProperty(CostantesConfig.MAXIMAS_LLAMADAS);
            this.tiempoMaximoLlamadas = properties.getProperty(CostantesConfig.TIEMPO_MAXIMO_LLAMADAS);
            this.tiempoLimpiarLlamadas = properties.getProperty(CostantesConfig.TIEMPO_LIMPIAR_LLAMADAS);
        } catch (Exception ignored) {
        }
    }
}