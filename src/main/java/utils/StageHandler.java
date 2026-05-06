package utils;

import exceptions.ItemNotFoundException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class StageHandler {

    private static StageHandler instance;
    private Stage stage;

    public static synchronized StageHandler getSingletonInstance() {
        if (instance == null) {
            instance = new StageHandler();
        }
        return instance;
    }

    public void loadPage(String path) throws IOException {
        URL xmlUrl = getClass().getResource(path);

        if (xmlUrl == null) {
            throw new IOException("Impossibile trovare il file FXML al percorso: " + path);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(xmlUrl);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1000 , 700);

        if (this.stage == null) {
            throw new IllegalStateException("Stage non inizializzato!");
        }

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                this.stage.close();
            }
        });

        this.stage.setMinHeight(scene.getHeight());
        this.stage.setMinWidth(scene.getWidth());
        this.stage.setFullScreen(false);
        this.stage.setScene(scene);
        this.stage.setFullScreenExitHint("");
        this.stage.setResizable(true);
        this.stage.setTitle("Krusty No Dusty");
        this.stage.show();

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void loadCss(Scene scene) {
        try {
            String css = getClass().getResource("/view/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            throw new ItemNotFoundException("Attenzione: File CSS non trovato! Assicurati che sia in src/main/resources");
        }
    }
}