package domain.errores;

public class DataFailureException extends RuntimeException {

    public DataFailureException(String message) {
        super(message);
    }
}

