package domain.errores;

public class ConnectionDBException extends RuntimeException {

    public ConnectionDBException(String message) {
        super(message);
    }
}

