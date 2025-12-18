package view.factory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class GraphicsFactory extends Application {

    protected FXMLLoader loader;
    protected Parent root;
    protected Scene scene;
    protected Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
                                    // Chiamata al metodo astratto che verrà implementato dalle sottoclassi
        load_interface();
    }

    public abstract void load_interface() throws IOException;
}