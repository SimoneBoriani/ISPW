package controller;

import app.SessionSingleton;
import bean.LoginBean;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.utente.Utente;

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

    @FXML
    private Button logInButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label ownedCarsLabel;

    @FXML
    private Button addCarButton;

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

        String str = "/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void switchLoginLabel(MouseEvent event) throws IOException {

        String str = "/controller/Login.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        show();
    }

    @FXML
    public void switchMainLabel(MouseEvent event) throws IOException {

        String str = "/controller/PrincipalPage.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void switchMainButton(ActionEvent event) throws IOException {

        String str = "/controller/PrincipalPage.fxml";
        scene = new Scene(change(str));

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Main");
        show();
    }

    @FXML
    public void switchRegister(MouseEvent event) throws IOException {

        String str = "/controller/Register.fxml";
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
                String str = "/controller/Profilo.fxml";
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

        String user = regUsername.getText().trim();
        String pass = regPassword.getText().trim();
        String confPass = confRegPassword.getText().trim();


        if (user.isEmpty() || pass.isEmpty() || confPass.isEmpty()) {
            errorText.setText("Errore: Campi vuoti!");
            return;
        }

        if (!pass.equals(confPass)) {
            errorText.setText("Le password non coincidono!");
            regPassword.clear();
            confRegPassword.clear();
            return;
        }

        LoginBean loginBean = new LoginBean(user, pass);
        LogInController logInController = new LogInController();

        Utente utenteEsistente = logInController.researchUser(loginBean);

        if (utenteEsistente != null) {

            regUsername.clear();
            regPassword.clear();
            confRegPassword.clear();
            errorText.setText("Errore: Username già registrato!");

        } else {

            logInController.insert(loginBean);
            switchMainButton(event);
        }
    }

    @FXML
    public void logIn(ActionEvent event) throws IOException {

        String user = username.getText().trim();
        String pass = password.getText().trim();

        LoginBean loginBean = new LoginBean(user, pass);
        LogInController logInController = new LogInController();
        Utente utente = logInController.researchUser(loginBean);


        if (user.isEmpty() || pass.isEmpty()) {

            errorText.setText("Errore: Campi vuoti!");

        } else if (null != utente) {

            if (user.equals(utente.getUsername()) && pass.equals(utente.getUserPassword())) {

                SessionSingleton.getInstance().setUtenteCorrente(utente);
                switchMainButton(event);
            }
        } else {

            username.clear();
            password.clear();
            errorText.setText("Username e/o Password non coincidono!");
        }
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        SessionSingleton.getInstance().logout();
        switchMainButton(event);
    }

    private void loaderCatalog() {

        try {
            String str = "/controller/catalogo.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(str));
            Parent catalogoRoot = loader.load();
            catalogo.getChildren().clear();
            catalogo.getChildren().add(catalogoRoot);
        } catch (IOException e) {
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
        if (logInButton != null && logOutButton != null) {

            Utente utenteLoggato = SessionSingleton.getInstance().getUtenteCorrente();

            if (utenteLoggato != null) {

                logInButton.setVisible(false);
                logInButton.setManaged(false);

                logOutButton.setVisible(true);
                logInButton.setManaged(true);
            } else {

                logInButton.setVisible(true);
                logInButton.setManaged(true);

                logOutButton.setVisible(false);
                logOutButton.setManaged(false);
            }
        }
        if (userNameLabel != null && nameLabel != null && surnameLabel != null && ownedCarsLabel != null && balanceLabel != null && roleLabel != null) {
            if (SessionSingleton.getInstance().isUserLoggedIn()) {
                Utente utenteLoggato = SessionSingleton.getInstance().getUtenteCorrente();
                userNameLabel.setText(utenteLoggato.getUsername());
                nameLabel.setText(utenteLoggato.getNome());
                surnameLabel.setText(utenteLoggato.getCognome());
                ownedCarsLabel.setText(String.valueOf(utenteLoggato.getAutoPossedute()));
                balanceLabel.setText(String.valueOf(utenteLoggato.getSaldo()));
                roleLabel.setText(utenteLoggato.getRuolo());
            }
        }
        if (null != addCarButton) {
            Utente utenteLoggato = SessionSingleton.getInstance().getUtenteCorrente();
            if (utenteLoggato != null) {
                if (utenteLoggato.getRuolo().equals("ADMIN")) {
                    addCarButton.setVisible(true);
                    addCarButton.setManaged(true);
                } else {
                    addCarButton.setVisible(false);
                    addCarButton.setManaged(false);
                }
            }
        }
    }
}
