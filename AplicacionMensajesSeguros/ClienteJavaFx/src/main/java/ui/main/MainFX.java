package ui.main;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.pantallas.common.ConstantesPantallas;
import ui.pantallas.principal.PrincipalController;

import java.io.IOException;
import java.util.ResourceBundle;

public class MainFX {

    @Inject
    FXMLLoader fxmlLoader;

    public void start(@Observes @StartupScene Stage stage) throws IOException {
        try {

            ResourceBundle r = ResourceBundle.getBundle(ConstantesPantallas.I_18_N_TEXTOS_UI);

            fxmlLoader.setResources(r);
            Parent fxmlParent = fxmlLoader.load(getClass().getResourceAsStream(ConstantesPantallas.FXML_PRINCIPAL_FXML));
            PrincipalController controller = fxmlLoader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(fxmlParent);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.getScene().getStylesheets().add(getClass().getResource(ConstantesPantallas.CSS_STYLE_CSS).toExternalForm());
            stage.setTitle(r.getString(ConstantesPantallas.APP_TITLE));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
