package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;



public class PageController {
    @FXML
    private ContextMenu menu;
    private Scene scene;
    private Stage stage;

    public void show() {
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.show();
    }
    public Parent change(String str) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(str)));
    }





    @FXML
    public void SwitchloginButton(ActionEvent event) throws IOException {

        String str="/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void SwitchloginLabel(MouseEvent event) throws IOException {

        String str="/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void SwitchMain(MouseEvent event) throws IOException {

        String str="/controller/PrincipalPage.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void SwitchRegister(MouseEvent event) throws IOException {

        String str= "/controller/Register.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Register");
        show();
    }

    public void Clickable_Image(MouseEvent event) {
        MenuItem voce1 = null;
        if (menu == null) {
            menu = new ContextMenu();

            voce1 = new MenuItem("Profilo");
            MenuItem voce2 = new MenuItem("Impostazioni");
            MenuItem voce3 = new MenuItem("Cerca la tua auto dei sogni");
            MenuItem voce4 = new MenuItem("Lista desideri");

            menu.getItems().addAll(voce1, voce2, voce3, voce4);
        }
        Node sorgente = (Node) event.getSource();
        menu.show(sorgente, event.getScreenX(), event.getScreenY());
        menu.setAutoHide(true);

        assert voce1 != null;
        voce1.setOnAction(e -> {
            try {
                String str="/controller/Profilo.fxml";
                Stage stage = (Stage) sorgente.getScene().getWindow();
                stage.setScene(new Scene(change(str)));
                stage.setFullScreen(true);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}

