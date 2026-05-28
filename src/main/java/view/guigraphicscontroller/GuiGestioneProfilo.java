package view.guigraphicscontroller;

import bean.NotificaBean;
import bean.ProfileBean;
import controller.GestioneProfiloController;
import controller.NotificheController;
import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.utente.Utente;
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;

import bean.PaymentTransactionBean;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import service.StripeService;
import service.IdentityService;
import utils.ConfigLoader;

import java.io.IOException;

public class GuiGestioneProfilo {

    private final GestioneProfiloController controller = ControllerFactory.getGraphicalSingletonFactory().createGestioneProfiloController();
    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

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

    private static final String BUTTON = "Button";
    private static final String ORANGE="orange";

    @FXML
    public void initialize() {
        if (SessionSingleton.getInstance().getUtenteCorrente() != null) {
            personalInfo();
        }
    }

    @FXML
    public void btnSaldo(ActionEvent actionEvent) throws IOException {
        if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
            StageHandler.getSingletonInstance().loadPage("/view/Login.fxml");
            return;
        }

        Stage popupStage = creaPopup("Ricarica Saldo");

        Label titolo = new Label("💳 Inserisci l'importo da ricaricare");
        titolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label sottotitolo = new Label("Pagamento sicuro tramite Stripe");
        sottotitolo.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

        Label lblStato = new Label("");
        lblStato.setWrapText(true);
        lblStato.setAlignment(Pos.CENTER);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setVisible(false);
        spinner.setPrefSize(30, 30);

        TextField txtImporto = new TextField();
        txtImporto.setPromptText("Es. 15.50");
        txtImporto.setMaxWidth(150);
        txtImporto.setAlignment(Pos.CENTER);

        Button btnProcedi = new Button("Procedi al pagamento");
        btnProcedi.setMaxWidth(200);
        btnProcedi.getStyleClass().add(BUTTON);

        Button btnAnnulla = new Button("Chiudi");
        btnAnnulla.setOnAction(e -> popupStage.close());

        btnProcedi.setOnAction(e -> elaboraPagamento(txtImporto, btnProcedi, spinner, lblStato, popupStage));

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        VBox inputBox = new VBox(10, txtImporto, btnProcedi);
        inputBox.setAlignment(Pos.CENTER);

        HBox statoBox = new HBox(10, spinner, lblStato);
        statoBox.setAlignment(Pos.CENTER);

        layoutPopup.getChildren().addAll(titolo, sottotitolo, new Label(""), inputBox, statoBox, btnAnnulla);

        Scene scene = new Scene(layoutPopup, 360, 480);
        StageHandler.getSingletonInstance().loadCss(scene);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void elaboraPagamento(TextField txtImporto, Button btnProcedi, ProgressIndicator spinner, Label lblStato, Stage popupStage) {
        String importoStr = txtImporto.getText().replace(",", ".");
        double importo;

        try {
            importo = Double.parseDouble(importoStr);
            if (importo <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            impostaMessaggioStato(lblStato, "❌ Inserisci un importo valido (es. 10.50)", "red");
            return;
        }

        cambiaStatoCaricamentoUI(txtImporto, btnProcedi, spinner, true);
        impostaMessaggioStato(lblStato, "Apertura Stripe Checkout nel browser...\nCompleta il pagamento e torna qui.", "blue");

        String username = SessionSingleton.getInstance().getUtenteCorrente().getUsername();
        StripeService stripe = new StripeService(ConfigLoader.get("stripe.secret.key"), ConfigLoader.getInt("stripe.success.port"));

        Task<PaymentTransactionBean> task = new Task<>() {
            @Override
            protected PaymentTransactionBean call() throws Exception {
                return stripe.avviaRicarica(username, importo);
            }
        };

        task.setOnSucceeded(ev -> gestisciSuccessoTask(task.getValue(), txtImporto, btnProcedi, spinner, lblStato, popupStage));
        task.setOnFailed(ev -> gestisciErroreTask(task.getException(), txtImporto, btnProcedi, spinner, lblStato));

        new Thread(task).start();
    }

    private void gestisciSuccessoTask(PaymentTransactionBean tx, TextField txtImporto, Button btnProcedi, ProgressIndicator spinner, Label lblStato, Stage popupStage) {
        cambiaStatoCaricamentoUI(txtImporto, btnProcedi, spinner, false);

        switch (tx.getPaymentStatus()) {
            case "paid":
                try {
                    aggiornaSaldoEGeneraNotifica(tx.getAmount());
                    impostaMessaggioStato(lblStato, "✅ Ricarica di " + tx.getAmount() + "€ completata!", "green");
                    ricaricaPaginaProfiloConRitardo(popupStage);
                } catch (Exception ex) {
                    impostaMessaggioStato(lblStato, "Errore aggiornamento saldo: " + ex.getMessage(), "red");
                    cambiaStatoCaricamentoUI(txtImporto, btnProcedi, spinner, false);
                }
                break;
            case "cancelled":
                impostaMessaggioStato(lblStato, "⚠ Pagamento annullato", ORANGE);
                break;
            default:
                impostaMessaggioStato(lblStato, "❌ Pagamento non riuscito (stato: " + tx.getPaymentStatus() + ")", "red");
                break;
        }
    }

    private void gestisciErroreTask(Throwable err, TextField txtImporto, Button btnProcedi, ProgressIndicator spinner, Label lblStato) {
        cambiaStatoCaricamentoUI(txtImporto, btnProcedi, spinner, false);
        impostaMessaggioStato(lblStato, "❌ Errore: " + (err != null ? err.getMessage() : "sconosciuto"), "red");
    }

    private void aggiornaSaldoEGeneraNotifica(double importoRicaricato) {
        int idUser = SessionSingleton.getInstance().getUtenteCorrente().getIdUser();
        double nuovoSaldo = SessionSingleton.getInstance().getUtenteCorrente().getSaldo() + importoRicaricato;

        ProfileBean bean = new ProfileBean();
        bean.setId(idUser);
        bean.setSaldo(nuovoSaldo);

        controller.updateSaldo(bean);
        SessionSingleton.getInstance().getUtenteCorrente().setSaldo(nuovoSaldo);

        NotificaBean saldo = new NotificaBean();

        saldo.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
        saldo.setMsg("Saldo aggiornato!");

        notificheController.generaNotificaSistema(saldo);
    }

    private void ricaricaPaginaProfiloConRitardo(Stage popupStage) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Platform.runLater(() -> {
                popupStage.close();
                try {
                    StageHandler.getSingletonInstance().loadPage("/view/Profilo.fxml");
                } catch (IOException ex) {
                    throw new GenericSystemException("Pagina non caricata:",ex);
                }
            });
        }).start();
    }

    @FXML
    public void btnVerificaPatente(ActionEvent actionEvent) {
        if (SessionSingleton.getInstance().getUtenteCorrente() == null) return;

        if (SessionSingleton.getInstance().getUtenteCorrente().getVerificato()) {
            Stage popupGiaVerificato = creaPopup("Verifica non necessaria");
            Label msg = new Label("La tua patente è già stata verificata con successo!");
            msg.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-padding: 20;");
            popupGiaVerificato.setScene(new Scene(msg));
            popupGiaVerificato.showAndWait();
            return;
        }

        Stage popupStage = creaPopup("Verifica Documento");
        Label lblStato = new Label("La verifica è necessaria per noleggiare veicoli.");
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setVisible(false);
        spinner.setPrefSize(25, 25);

        Button btnInizia = new Button("Inizia Verifica Online");
        btnInizia.setPrefWidth(220);
        btnInizia.getStyleClass().add(BUTTON);

        btnInizia.setOnAction(e -> avviaFlussoVerifica(btnInizia, spinner, lblStato, popupStage));
        mostraLayoutVerifica(popupStage, lblStato, spinner, btnInizia);
    }

    private void avviaFlussoVerifica(Button btn, ProgressIndicator sp, Label lbl, Stage stage) {
        cambiaStatoCaricamentoUI(btn, sp, true);
        impostaMessaggioStato(lbl, "Apertura browser in corso...\nCompleta la verifica e torna qui.", "blue");
        ProfileBean bean = new ProfileBean();
        bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return controller.avviaVerificaPatente(bean);
            }
        };

        task.setOnSucceeded(ev -> {
            cambiaStatoCaricamentoUI(btn, sp, false);
            String stato = task.getValue();

            if ("verified".equals(stato)) {
                impostaMessaggioStato(lbl, "✅ Patente verificata con successo!", "green");
                controller.completaVerificaPatente(bean);

                new Thread(() -> {
                    try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    Platform.runLater(stage::close);
                }).start();

            } else if ("processing".equals(stato)) {
                impostaMessaggioStato(lbl, "⏳ Verifica in corso. Ci vorrà qualche minuto.", ORANGE);
            } else if ("requires_input".equals(stato)) {
                impostaMessaggioStato(lbl, "❌ Verifica fallita. Riprova con foto più nitide.", "red");
            } else {
                impostaMessaggioStato(lbl, "⚠ Operazione annullata.", ORANGE);
            }
        });

        task.setOnFailed(ev -> {
            cambiaStatoCaricamentoUI(btn, sp, false);
            impostaMessaggioStato(lbl, "Errore di connessione: " + task.getException().getMessage(), "red");
        });

        new Thread(task).start();
    }

    private void mostraLayoutVerifica(Stage popupStage, Label lblStato, ProgressIndicator spinner, Button btnInizia) {
        Label iconaDoc = new Label("🪪");
        iconaDoc.setStyle("-fx-font-size: 50px;");

        Label titolo = new Label("Verifica Identità");
        titolo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label infoPrivacy = new Label("I tuoi dati sono protetti da crittografia AES-256.\nNon memorizziamo foto sui nostri server.");
        infoPrivacy.setStyle("-fx-text-fill: gray; -fx-font-size: 10px; -fx-text-alignment: center;");
        infoPrivacy.setWrapText(true);

        HBox statoBox = new HBox(10, spinner, lblStato);
        statoBox.setAlignment(Pos.CENTER);
        statoBox.setPadding(new Insets(10, 0, 10, 0));

        Button btnChiudi = new Button("Annulla");
        btnChiudi.setOnAction(e -> popupStage.close());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setPrefWidth(380);

        layout.getChildren().addAll(iconaDoc, titolo, infoPrivacy, btnInizia, statoBox, btnChiudi);

        Scene scene = new Scene(layout);
        if (StageHandler.getSingletonInstance() != null) {
            StageHandler.getSingletonInstance().loadCss(scene);
        }

        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private Stage creaPopup(String titolo) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(titolo);
        stage.setResizable(false);
        return stage;
    }

    private void impostaMessaggioStato(Label label, String messaggio, String colore) {
        label.setStyle("-fx-text-fill: " + colore + "; -fx-font-weight: bold;");
        label.setText(messaggio);
    }

    private void cambiaStatoCaricamentoUI(TextField txtImporto, Button btnProcedi, ProgressIndicator spinner, boolean inCaricamento) {
        txtImporto.setDisable(inCaricamento);
        btnProcedi.setDisable(inCaricamento);
        spinner.setVisible(inCaricamento);
    }
    private void cambiaStatoCaricamentoUI(Button btnProcedi, ProgressIndicator spinner, boolean inCaricamento) {
        btnProcedi.setDisable(inCaricamento);
        spinner.setVisible(inCaricamento);
    }

    @FXML
    public void btnPassword(ActionEvent actionEvent){
        //Implements function
    }

    public void personalInfo() {
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

    private void updateProfile() {
        Stage popupStage = creaPopup("Modifica Profilo");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome");

        TextField txtCognome = new TextField();
        txtCognome.setPromptText("Cognome");

        Button btnUpdate = new Button("Aggiorna");
        btnUpdate.getStyleClass().add(BUTTON);

        btnUpdate.setOnAction(e -> {
            ProfileBean bean = new ProfileBean();
            bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());

            String username = txtUsername.getText().trim();
            if (!username.isEmpty()) bean.setUsername(username);

            String nome = txtNome.getText().trim();
            if (!nome.isEmpty()) bean.setNome(nome);

            String cognome = txtCognome.getText().trim();
            if (!cognome.isEmpty()) bean.setCognome(cognome);

            try {
                controller.updateProfile(bean);

                NotificaBean info = new NotificaBean();
                info.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
                info.setMsg("Informazioni personali aggiornate");


                notificheController.generaNotificaSistema(info);
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
        String reload = "/view/Profilo.fxml";
        StageHandler.getSingletonInstance().loadPage(reload);
    }

    @FXML
    public void goToHome(MouseEvent event) throws IOException {
        String str = "/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }
}