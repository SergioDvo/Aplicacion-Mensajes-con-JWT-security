package domain.servicios;

import dao.DaoCarpetas;
import dao.DaoMensajes;
import domain.errores.DataFailureException;
import jakarta.inject.Inject;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import org.example.security.Encriptacion;

import java.util.List;

public class ServicesMensajesCajas {
    private final DaoMensajes daoMensajesCajas;
    private final DaoCarpetas daoCarpetas;
    @Inject
    private ServicesMensajesCajas(DaoMensajes daoMensajesCajas,DaoCarpetas daoCarpetas) {
        this.daoMensajesCajas = daoMensajesCajas;
        this.daoCarpetas = daoCarpetas;
    }
    public List<MensajeCaja> getMensajesCajas(String carpeta, String usuario, String contrasena) {
        return daoMensajesCajas.getAllByCajas(carpeta, usuario, contrasena);
    }
    public List<MensajeCaja> getMensajesCajasCompartida(String usuario,String carpeta,String contrasena){
        return daoMensajesCajas.getMensajesCajasCompartida(usuario,carpeta,contrasena);
    }
    public List<String> getCajasFromUser(String usuario) {
        return daoCarpetas.getAllCajasFromUser(usuario);
    }
    public MensajeCaja añadirMensajeCaja(MensajeCaja mensajeCaja) {
        return daoMensajesCajas.añadirMensajeCaja(mensajeCaja);
    }
    public MensajeCaja updateMensajeCaja(MensajeCaja mensajeCaja) {
        return daoMensajesCajas.updateMensajeCaja(mensajeCaja);
    }
    public MensajeCaja updateMensajeCajaCompartida(MensajeCaja mensajeCaja) {
        return daoMensajesCajas.updateMensajeCajaCompartida(mensajeCaja);
    }
    public void eliminarMensajeCaja(int id, String carpeta, String contrasena) {
        daoMensajesCajas.eliminarMensajeCaja(id,carpeta,contrasena);
    }
    public Carpeta añadirCaja(Carpeta carpeta) {
        return daoCarpetas.añadirCarpeta(carpeta);
    }
    public Carpeta updateCaja(Carpeta carpeta,String contrasena) {
        return daoCarpetas.updateContrasena(carpeta,contrasena);
    }

}
