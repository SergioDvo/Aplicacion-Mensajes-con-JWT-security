package dao;

import JDBC.DBConnectionPool;
import dao.constant.SQLQueries;
import domain.errores.ConnectionDBException;
import domain.errores.DataFailureException;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class DaoMensajes {
    private final DBConnectionPool pool;

    @Inject
    public DaoMensajes(DBConnectionPool pool) {
        this.pool = pool;
    }
    public List<MensajeCaja> getAllByCajas(String caja,String usuario,String contrasena) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            List<MensajeCaja> mensajeCajaList =jtm.query(SQLQueries.SELECT_MENSAJE_BY_CAJA, BeanPropertyRowMapper.newInstance(MensajeCaja.class),usuario,caja,contrasena);
            if (mensajeCajaList.isEmpty()) {
                throw new DataFailureException(ConstantesRest.NO_HAY_MENSAJES_EN_LA_CAJA_O_CREDENCIALES_ERRONEAS);
            }
            return mensajeCajaList;
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public List<MensajeCaja> getMensajesCajasCompartida(String usuario,String carpeta,String contrasena){
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            return jtm.query(SQLQueries.SELECT_MENSAJE_BY_CAJA_COMPARTIDA, BeanPropertyRowMapper.newInstance(MensajeCaja.class),usuario,carpeta,contrasena);
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public MensajeCaja añadirMensajeCaja(MensajeCaja mensajeCaja) {
        try {
            KeyHolder id = new GeneratedKeyHolder();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(pool.getDataSource());
            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQLQueries.INSERT_MENSAJE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, mensajeCaja.getMensaje());
                ps.setString(2, mensajeCaja.getNombreCarpeta());
                ps.setString(3, mensajeCaja.getNombreLogin());
                return ps;
            }, id);
            if (rowsAffected == 0) {
                throw new DataFailureException(ConstantesRest.NO_SE_HA_PODIDO_INSERTAR_EL_MENSAJE);
            } else {
                mensajeCaja.setId(Objects.requireNonNull(id.getKey()).intValue());
                return mensajeCaja;
            }
        } catch (DataAccessException e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public MensajeCaja updateMensajeCaja(MensajeCaja mensajeCaja) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            jtm.update(SQLQueries.UPDATE_MENSAJE, mensajeCaja.getMensaje(), mensajeCaja.getId());
            return mensajeCaja;
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public MensajeCaja updateMensajeCajaCompartida(MensajeCaja mensajeCaja) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            int rowAffected = jtm.update(SQLQueries.UPDATE_MENSAJE_COMPARTIDA, mensajeCaja.getMensaje(), mensajeCaja.getId(),mensajeCaja.getNombreCarpeta());
            if (rowAffected == 0) {
                throw new DataFailureException(ConstantesRest.NO_SE_HA_PODIDO_ACTUALIZAR_EL_MENSAJE);
            } else {
                return mensajeCaja;
            }
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public void eliminarMensajeCaja(int id,String carpeta,String contrasena) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(pool.getDataSource());
            int rowsAffected = jdbcTemplate.update(SQLQueries.DELETE_MENSAJE, id,carpeta,contrasena);
            if (rowsAffected == 0) {
                throw new DataFailureException(ConstantesRest.NO_SE_HA_PODIDO_INSERTAR_EL_MENSAJE);
            }
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }

}
