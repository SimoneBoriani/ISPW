package controller;

import bean.LoginBean;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class PageController {

    @FXML
    private ContextMenu menu;

    @FXML
    private TextField regUsername;

    @FXML
    private TextField regPassword;

    @FXML
    private TextField confRegPassword;

    @FXML
    private Label errorText;

    @FXML
    private Label errorCatalogText;

    @FXML
    private VBox catalogo;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    Logger logger = Logger.getLogger(getClass().getName());
    private Scene scene;
    private Stage stage;

    public void show() {
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.setMinHeight(700);
        stage.setMinWidth(700);
        stage.show();
    }
    public Parent change(String str) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(str)));
    }

    @FXML
    public void switchLoginButton(ActionEvent event) throws IOException {

        String str="/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void switchLoginLabel(MouseEvent event) throws IOException {

        String str="/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void switchMainLabel(MouseEvent event) throws IOException {

        String str="/controller/PrincipalPage.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void switchMainButton(ActionEvent event) throws IOException {

        String str="/controller/PrincipalPage.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void switchRegister(MouseEvent event) throws IOException {

        String str= "/controller/Register.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Register");
        show();
    }

    @FXML
    public void aggiungiAuto(ActionEvent event) throws IOException {

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

    public void clickableImage(MouseEvent event) {
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
                stage = (Stage) sorgente.getScene().getWindow();
                stage.setScene(new Scene(change(str)));
                stage.setFullScreen(true);
                stage.show();
            } catch (IOException ex) {
                logger.info(ex.getMessage());
            }
        });
    }

    @FXML
    public void regUser(ActionEvent event) throws IOException {

        String user = regUsername.getText();
        String pass = regPassword.getText();
        String confPass = confRegPassword.getText();

        if (user.isEmpty() || pass.isEmpty() || confPass.isEmpty()) {
            errorText.setText("Errore: Campi vuoti!");
        } else if (!pass.equals(confPass)) {
            errorText.setText("Le password non coincidono!");
            regPassword.clear();
            confRegPassword.clear();
        } else {

            LoginBean loginBean= new LoginBean(user,pass);
            LogInController logInController= new LogInController();
            logInController.insert(loginBean);
            switchMainButton(event);
        }
    }


    private void loaderCatalog(){

        try {
            String str = "/controller/catalogo.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(str));
            Parent catalogoRoot = loader.load();
            catalogo.getChildren().clear();
            catalogo.getChildren().add(catalogoRoot);
        }catch (IOException e){
            throw new IllegalArgumentException();
        }

    }
    @FXML
    public void initialize() {
        try {
            loaderCatalog();
        } catch (Exception e) {
           logger.info(e.getMessage());
            if (errorCatalogText != null) {
                errorCatalogText.setText("Nessun catalogo trovato");
            }
        }
    }
}

