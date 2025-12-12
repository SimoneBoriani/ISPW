package view.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class PrincipalPageApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PrincipalPage.fxml"));
        Scene scene =new Scene(loader.load());
        stage.setTitle("Main");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
