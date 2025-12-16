package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class PageController {

    private Scene scene;
    private Stage stage;
    private Parent root;

    public void Switchlogin(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/controller/Login.fxml"));
        scene = new Scene(root);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.show();

    }

    public void SwitchMain(MouseEvent event) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/controller/PrincipalPage.fxml")));
        scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.show();
        }
    }