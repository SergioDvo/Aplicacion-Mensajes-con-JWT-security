package ui.pantallas.principal;


import io.github.palexdev.materialfx.font.MFXFontIcon;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.log4j.Log4j2;
import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import servicios.ServiciosLogin;
import ui.pantallas.common.BasePantallaController;
import ui.pantallas.common.ConstantesPantallas;
import ui.pantallas.common.Pantallas;

import java.io.*;
import java.security.SecureRandom;
import java.util.Optional;

@Log4j2
public class PrincipalController {

    Instance<Object> instance;
    private Stage primaryStage;
    @FXML
    public BorderPane root;

    private double xOffset;

    private double yOffset;

    @FXML
    private HBox menuPrincipal;
    @FXML
    private MFXFontIcon cerrar;


    private Alert alert;

    private String loginUser;

    private ServiciosLogin serviciosLogin;

    private String clavePublicServidor;

    private String clavePrivadaServidor;


    @Inject
    public PrincipalController(Instance<Object> instance, ServiciosLogin serviciosLogin) {
        this.instance = instance;
        this.serviciosLogin = serviciosLogin;
        alert = new Alert(Alert.AlertType.NONE);
    }

    private void cargarPantalla(Pantallas pantalla) {
        cambioPantalla(cargarPantalla(pantalla.getRuta()));
    }

    public void sacarAlertError(String mensaje) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void sacarAlertInformacion(String mensaje) {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    private Pane cargarPantalla(String ruta) {
        Pane panePantalla = null;
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setControllerFactory(controller -> instance.select(controller).get());
            panePantalla = fxmlLoader.load(getClass().getResourceAsStream(ruta));
            BasePantallaController pantallaController = fxmlLoader.getController();
            pantallaController.setPrincipalController(this);
            pantallaController.principalCargado();


        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return panePantalla;
    }


    private void cambioPantalla(Pane pantallaNueva) {
        root.setCenter(pantallaNueva);
    }


    public void initialize() {
        menuPrincipal.setVisible(true);
        menuPrincipal.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        menuPrincipal.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });
        cargarPantalla(Pantallas.PANTALLALOGIN);
    }

    private void closeWindowEvent(WindowEvent event) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.CANCEL);
        alert.getButtonTypes().add(ButtonType.YES);
        alert.setTitle(ConstantesPantallas.QUIT_APPLICATION);
        alert.setContentText(ConstantesPantallas.CLOSE_WITHOUT_SAVING);
        alert.initOwner(primaryStage.getOwner());
        Optional<ButtonType> res = alert.showAndWait();
        res.ifPresent(buttonType -> {
            if (buttonType == ButtonType.CANCEL) {
                event.consume();
            }
        });
    }

    public void exit() {
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
    }

    @FXML
    private void menuClick(ActionEvent actionEvent) {
        switch (((MenuItem) actionEvent.getSource()).getId()) {
            case "newspapers":
                cargarPantalla(Pantallas.PANTALLANEWS);
                break;
            case "readers":
                cargarPantalla(Pantallas.PANTALLAREADERS);
                break;
            case "logout":
                logout();
                break;
            case "mensajeria":
                cargarPantalla(Pantallas.PANTALLAMENSAJERIA);
                break;
            case "añadirUsuario":
                cargarPantalla(Pantallas.PANTALLAADDUSER);
                break;
            case "mensajeriaOnline":
                cargarPantalla(Pantallas.PANTALLAMENSAJERIAONLINE);
                break;
            default:
                cargarPantalla(Pantallas.PANTALLAINICIO);
                break;
        }
    }

    public void onLoginHecho() {
        cargarPantalla(Pantallas.PANTALLAINICIO);
    }

    public void setLogin(String login) {
        loginUser = login;
    }

    public String getLogin() {
        return loginUser;
    }

    public void logout() {
        serviciosLogin.logout().observeOn(JavaFxScheduler.platform()).subscribe();
        loginUser = "";
        cargarPantalla(Pantallas.PANTALLALOGIN);
    }
}
