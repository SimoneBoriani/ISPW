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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;



public class PageController {
    @FXML
    private ContextMenu menu;
    private MenuItem voce1,voce2,voce3,voce4,voce5;
    private Scene scene;
    private Stage stage;

    public void show() {
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void SwitchloginButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/controller/Login.fxml")));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void SwitchloginLabel(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/controller/Login.fxml")));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void SwitchMain(MouseEvent event) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/controller/PrincipalPage.fxml")));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void SwitchRegister(MouseEvent event) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/controller/register.fxml")));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Register");
        show();
    }

    public void Clickable_Image(MouseEvent event){
        if (menu == null) {
            menu = new ContextMenu();

             voce1 = new MenuItem("Profilo");
             voce2 = new MenuItem("Impostazioni");
             voce3 = new MenuItem("Cerca la tua auto dei sogni");
             voce4 = new MenuItem("Lista desideri");
             voce5 = new MenuItem("Logout");

            menu.getItems().addAll(voce1, voce2, voce3, voce4, voce5);
        }
            Node sorgente = (Node) event.getSource();
            menu.show(sorgente, event.getScreenX(), event.getScreenY());
            menu.setAutoHide(true);

        voce1.setOnAction(e -> {
            try {
                // Carica il nuovo FXML
                Parent root = FXMLLoader.load(getClass().getResource("/controller/Profilo.fxml"));
                // Ottieni la finestra attuale (Stage) partendo dal menu
                Stage stage = (Stage) sorgente.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}

