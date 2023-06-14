package ui.pantallas.login;

import lombok.Data;
import modelo.utils.Login;

@Data
public class LoginState {

    private final Login login;
    private final String error;

}
