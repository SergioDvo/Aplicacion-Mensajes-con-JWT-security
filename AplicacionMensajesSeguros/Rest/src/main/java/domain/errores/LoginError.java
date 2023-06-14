package domain.errores;

public class LoginError extends RuntimeException{

        public LoginError(String message) {
            super(message);
        }
}
