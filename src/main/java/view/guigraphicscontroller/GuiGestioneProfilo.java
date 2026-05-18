package view.guigraphicscontroller;

import bean.ProfileBean;
import controller.GestioneProfiloController;
import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.macchina.Macchina;
import model.utente.Utente;
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;

public class GuiGestioneProfilo {

    private final GestioneProfiloController controller= ControllerFactory.getGraphicalSingletonFactory().createGestioneProfiloController();

    @FXML
    private ListView<Macchina> carListView;


    @FXML
    private Label nomeUtenteLabel;
    @FXML
    private Label cognomeUtenteLabel;
    @FXML
    private Label usernameUtenteLabel;
    @FXML
    private Label saldoUtenteLabel;
    @FXML
    private Label roleUtenteLabel;

    @FXML
    public void initialize(){

        if(SessionSingleton.getInstance().getUtenteCorrente()!=null) {
                personalInfo();
        }
    }

    @FXML
    public void btnSaldo(ActionEvent actionEvent) throws IOException {

        String str="/view/Login.fxml";

        if(SessionSingleton.getInstance().getUtenteCorrente()!=null) {

            Stage popupStage = new Stage();

            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setTitle("Aggiungi Saldo");

            TextField txtCodice = new TextField();
            txtCodice.setPromptText("Codice Carta");

            TextField txtScadenza = new TextField();
            txtScadenza.setPromptText("Scadenza");

            TextField txtCvv = new TextField();
            txtCvv.setPromptText("CVV");

            TextField txtSaldo = new TextField();
            txtSaldo.setPromptText("Saldo");

            Button btnUpdate = new Button("Conferma");

            btnUpdate.getStyleClass().add("Button");

            btnUpdate.setOnAction(e -> {

                ProfileBean bean=new ProfileBean();


                bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());
                bean.setSaldo(Double.parseDouble(txtSaldo.getText()));
                controller.updateProfile(bean);
                String reload="/view/Profilo.fxml";
                try {
                    StageHandler.getSingletonInstance().loadPage(reload);
                } catch (IOException ex) {
                    throw new GenericSystemException("Errore:",ex);
                }
                popupStage.close();

            });

            VBox layoutPopup = new VBox(15);
            layoutPopup.setPadding(new Insets(20));
            layoutPopup.setAlignment(Pos.CENTER);

            layoutPopup.getChildren().addAll(
                    new Label("Aggiorna Dati Personali:"),
                    txtCodice,
                    txtScadenza,
                    txtCvv,
                    txtSaldo,
                    btnUpdate
            );

            Scene scene = new Scene(layoutPopup, 300, 350);

            StageHandler.getSingletonInstance().loadCss(scene);

            popupStage.setScene(scene);
            popupStage.showAndWait();

            }else StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void btnPassword(ActionEvent actionEvent){
        //Implements function
    }

    public void personalInfo(){

            Utente utenteCorrente = SessionSingleton.getInstance().getUtenteCorrente();

            if (utenteCorrente != null) {

                nomeUtenteLabel.setText(utenteCorrente.getNome() != null ? utenteCorrente.getNome() : "");

                cognomeUtenteLabel.setText(utenteCorrente.getCognome() != null ? utenteCorrente.getCognome() : "");

                usernameUtenteLabel.setText(utenteCorrente.getUsername() != null ? utenteCorrente.getUsername() : "");

                saldoUtenteLabel.setText(String.valueOf(utenteCorrente.getSaldo()));

                roleUtenteLabel.setText(utenteCorrente.getRuolo() != null ? utenteCorrente.getRuolo() : "USER");

            } else {

                nomeUtenteLabel.setText("");
                cognomeUtenteLabel.setText("");
                usernameUtenteLabel.setText("");
                saldoUtenteLabel.setText("");
            }
        }

    private void updateProfile(){

        Stage popupStage = new Stage();

        popupStage.initModality(Modality.APPLICATION_MODAL);

        popupStage.setTitle("Modifica Profilo");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome");

        TextField txtCognome = new TextField();
        txtCognome.setPromptText("Cognome");

        Button btnUpdate = new Button("Aggiorna");

        btnUpdate.getStyleClass().add("Button");

        btnUpdate.setOnAction(e -> {

            ProfileBean bean=new ProfileBean();
            bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());

            String username = txtUsername.getText().trim();
            if (!username.isEmpty()) {
                bean.setUsername(username);
            }

            String nome = txtNome.getText().trim();
            if (!nome.isEmpty()) {
                bean.setNome(nome);
            }

            String cognome = txtCognome.getText().trim();
            if (!cognome.isEmpty()) {
                    bean.setCognome(cognome);
            }

            try {
                controller.updateProfile(bean);
            } catch (Exception ex) {
                throw new GenericSystemException("Errore aggiornamento parametri: ", ex);
            }

            popupStage.close();

        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        layoutPopup.getChildren().addAll(
                new Label("Aggiorna Dati Personali:"),
                txtUsername,
                txtNome,
                txtCognome,
                btnUpdate
        );

        Scene scene = new Scene(layoutPopup, 300, 350);

        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setScene(scene);
        popupStage.showAndWait();

    }
    @FXML
    public void btnUpdate(ActionEvent event) throws IOException {
        updateProfile();
        String reload="/view/Profilo.fxml";
        StageHandler.getSingletonInstance().loadPage(reload);
    }

    @FXML
    public void goToHome(MouseEvent event) throws IOException {
        String str="/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }
}