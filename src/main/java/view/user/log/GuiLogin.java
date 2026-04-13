package view.user.log;

import bean.LoginBean;
import controller.LogInController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import view.factory.GuiGraphicsFactory;
import view.sbcontroller.GuiPageManager;

public class GuiLogin extends LoginUi{

    private GuiPageManager navigator=GuiGraphicsFactory;
    private LogInController controller=GuiGraphicsFactory.createLoginController();
    private LoginBean credenziali=new LoginBean();

    @FXML
    private Label errorLabel;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    @FXML
    public void auth(ActionEvent event){
        log();
    }

    @FXML
    public void backHomeLabel(MouseEvent event) {
        navigator.switchHomeLabel(event);
    }

    @Override
    public void log() {


        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {

            errorLabel.setText("Attenzione: Inserisci username e password!");
            return;
        }

        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        try {

            controller.authenticate(credenziali);

            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Login effettuato con successo!");
            GuiPageManager.

        } catch (IllegalArgumentException e) {
            // Il Controller ha rilevato che la password è errata o l'utente non esiste
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText(e.getMessage());
            txtPassword.clear(); // Best practice UX: svuota solo la password se si sbaglia

        } catch (Exception e) {
            // Errore grave (es. database offline)
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Errore di sistema, riprova più tardi.");
        }
    }
}
