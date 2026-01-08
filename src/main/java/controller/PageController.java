package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;



public class PageController {

    @FXML
    private ContextMenu menu;

    @FXML
    private TextField Reg_username;

    @FXML
    private TextField Reg_password;

    @FXML
    private TextField Conf_Reg_password;

    @FXML
    private Label Error_text;

    @FXML
    private VBox catalogo;


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
    public void switchloginButton(ActionEvent event) throws IOException {

        String str="/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void switchloginLabel(MouseEvent event) throws IOException {

        String str="/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void switchMainlabel(MouseEvent event) throws IOException {

        String str="/controller/PrincipalPage.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void switchMainbutton(ActionEvent event) throws IOException {

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

    public void clickable_Image(MouseEvent event) {
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
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void GetInfo(ActionEvent event) throws IOException {
        String user = Reg_username.getText();
        String pass = Reg_password.getText();
        String confPass = Conf_Reg_password.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            Error_text.setText("Errore: Campi vuoti!");
        } else if (!pass.equals(confPass)) {
            Error_text.setText("Le password non coincidono!");
            Reg_password.clear();
            Conf_Reg_password.clear();
        } else {
            switchMainbutton(event);
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
            throw new RuntimeException(e);
        }

    }
    @FXML
    public void initialize(){
        try {
            loaderCatalog();
        } catch (Exception e) {
            Error_text.setText("Nessun catalogo");
            System.err.println("Catalogo non caricato: " + e.getMessage());
        }
    }
}

