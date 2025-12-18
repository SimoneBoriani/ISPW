package view.factory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public  class GuiGraphicsFactory extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/PrincipalPage.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Main");
        stage.setFullScreen(true);
        stage.setResizable(false);


        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo1.png")));
        stage.getIcons().add(logo);

        stage.setScene(scene);
        stage.show();
    }

}