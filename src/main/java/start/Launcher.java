package start;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.StageHandler;

public class Launcher extends Application {

    public static void main(String[] args) {
        Selection selection = new Selection();
        selection.init();
    }

    public static void starter(){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));

        StageHandler.getSingletonInstance().setStage(stage);

        StageHandler.getSingletonInstance().loadPage("/view/palle.fxml");
    }
}
