package view.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.windowmanager.GuiPageManager;

import java.io.IOException;

public class GuiAggiungiAuto extends GuiPageManager {

    @FXML
    public void openWindow(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/AggiungiAuto.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root);
        Stage newStage = new Stage();

        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(primaryStage);

        newStage.setScene(newScene);
        newStage.setTitle("Aggiungi Auto");
        newStage.setResizable(false);
        newStage.show();
    }
}
