package dao;

import JDBC.DBConnectionPool;
import dao.constant.SQLQueries;
import domain.errores.ConnectionDBException;
import domain.errores.DataFailureException;
import jakarta.inject.Inject;
import jakarta.rest.common.ConstantesRest;
import modelo.utils.Carpeta;
import modelo.utils.MensajeCaja;
import org.example.security.Encriptacion;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.*;
import java.util.List;
import java.util.Objects;

public class DaoCarpetas {
    private final DBConnectionPool pool;

    private final Encriptacion encriptacion;

    @Inject
    public DaoCarpetas(DBConnectionPool pool, Encriptacion encriptacion) {
        this.pool = pool;
        this.encriptacion = encriptacion;
    }

    public List<String> getAllCajasFromUser(String usuario) {
        List<Carpeta> carpetaList;
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            carpetaList = jtm.query(SQLQueries.SELECT_CAJAS_FROM_USER, BeanPropertyRowMapper.newInstance(Carpeta.class),usuario);
            return carpetaList.stream().map(Carpeta::getNombre).toList();
        } catch (Exception e) {
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public Carpeta añadirCarpeta(Carpeta carpeta) {
        try (Connection con = pool.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(SQLQueries.INSERT_CARPETA)) {
            preparedStatement.setString(1, carpeta.getNombre());
            preparedStatement.setString(2, carpeta.getContrasena());
            preparedStatement.setString(3, carpeta.getNombreLogin());
            preparedStatement.setString(4, carpeta.getEstado());
            preparedStatement.executeUpdate();
            return carpeta;
        } catch (SQLException e) {
            throw new DataFailureException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConnectionDBException(e.getMessage());
        }
    }
    public Carpeta updateContrasena(Carpeta carpeta,String contrasena) {
        try {
            JdbcTemplate jtm = new JdbcTemplate(pool.getDataSource());
            List<MensajeCaja> list = jtm.query(SQLQueries.SELECT_MENSAJE_BY_CAJA_COMPARTIDA, BeanPropertyRowMapper.newInstance(MensajeCaja.class),carpeta.getNombreLogin(),carpeta.getNombre(),carpeta.getContrasena());
            if (list.isEmpty()) {
                throw new DataFailureException(ConstantesRest.CREDENCIALES_NO_VALIDOS_O_CARPETA_VACIA);
            }
            list.forEach(mensajeCaja -> {
                String mensajeDesencriptado = encriptacion.desencriptar(mensajeCaja.getMensaje(),carpeta.getContrasena());
                String mensajeEncriptado = encriptacion.encriptar(mensajeDesencriptado,contrasena);
                jtm.update(SQLQueries.UPDATE_MENSAJE,mensajeEncriptado,mensajeCaja.getId());
            });
            jtm.update(SQLQueries.UPDATE_CONTRASENA_CARPETA ,contrasena,carpeta.getNombre(),carpeta.getNombreLogin());
            List<Carpeta> carpetaUpdated = jtm.query(SQLQueries.SELECT_CARPETA_BY_NOMBRE, BeanPropertyRowMapper.newInstance(Carpeta.class),carpeta.getNombre());
            return carpetaUpdated.get(0);
        } catch (Exception e) {
            throw new DataFailureException(e.getMessage());
        }
    }

}
