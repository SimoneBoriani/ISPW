package view.guigraphicscontroller;

import utils.SessionSingleton;
import bean.LoginBean;
import controller.LogInController;
import exceptions.IncorrectCredentialExeption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import utils.StageHandler;


import java.io.IOException;

public class GuiLoginController extends LogInController{


    @FXML
    private Label errorLabel;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtConfPass;
    @FXML
    private Button btnRegister;

    private static final String STYLE = "-fx-text-fill: red;";

    @FXML
    public void auth(ActionEvent event){

        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {

            errorLabel.setText("Attenzione: Inserisci username e/o password!");
            return;
        }

        LoginBean credenziali=new LoginBean();
        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        try {

            authenticate(credenziali);

            if(SessionSingleton.getInstance().getUtenteCorrente()!=null){

                StageHandler.getSingletonInstance().loadPage("/view/PrincipalPage-Catalogo.fxml");
            }



        } catch (IncorrectCredentialExeption e) {

            errorLabel.setStyle(STYLE);
            errorLabel.setText(e.getMessage());
            txtPassword.clear();

        } catch (Exception e) {

            errorLabel.setStyle(STYLE);
            errorLabel.setText("Errore di sistema, riprova più tardi.");

        }
    }

    @FXML
    public void register(ActionEvent event) throws IOException {

        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();
        String confPass = txtConfPass.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setStyle(STYLE);
            errorLabel.setText("Attenzione: Inserisci username e/o password!");
            return;
        }

        if(!pass.equals(confPass)){
            errorLabel.setStyle(STYLE);
            errorLabel.setText("Attenzione Le password non coincidono!");
            return;
        }

        LoginBean credenziali=new LoginBean();

        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        if(researchUser(credenziali)){

            errorLabel.setStyle(STYLE);
            errorLabel.setText("Utente già registrato!");

            }

        else{

            insert(credenziali);
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Registrazione effettuata con successo!");

            txtUsername.clear();
            txtPassword.clear();
            txtConfPass.clear();

        }
        }

    @FXML
    public void goToReg(MouseEvent event) throws IOException {

        String str="/view/Register.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void goToLog(MouseEvent event) throws IOException {

        String str="/view/Login.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void labelCatalogo(MouseEvent event) throws IOException {

        String str="/view/PrincipalPage-Catalogo.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

}