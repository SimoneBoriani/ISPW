package view.guigraphicscontroller;

import javafx.scene.control.*;
import utils.SessionSingleton;
import bean.ProfileBean;
import controller.LogInController;
import exceptions.IncorrectCredentialExeption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import utils.StageHandler;
import view.factory.ControllerFactory;


import java.io.IOException;

public class GuiLoginController{

    private final LogInController logInController= ControllerFactory.getGraphicalSingletonFactory().createLoginController();

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

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private CheckBox checkMostraPassword;

    private static final String STYLE = "-fx-text-fill: red;";

    @FXML
    public void auth(ActionEvent event){


        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {

            errorLabel.setText("Attenzione: Inserisci username e/o password!");
            return;
        }

        ProfileBean credenziali=new ProfileBean();
        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        try {

            String str;
            logInController.authenticate(credenziali);

            if(SessionSingleton.getInstance().getUtenteCorrente()!=null){
                if(SessionSingleton.getInstance().getUtenteCorrente().getRuolo().equals("USER")) {
                    str = "/view/CatalogoView.fxml";

                }else{
                    str = "/view/AdminView.fxml";
                }
                StageHandler.getSingletonInstance().loadPage(str);
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
    public void register(ActionEvent event){

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

        ProfileBean credenziali=new ProfileBean();

        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        if(logInController.researchUser(credenziali)){

            errorLabel.setStyle(STYLE);
            errorLabel.setText("Utente già registrato!");

            }

        else{

            logInController.insert(credenziali);
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

        String str= "/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        if (checkMostraPassword.isSelected()) {

            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {

            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

}