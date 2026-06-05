package view.guigraphicscontroller;

import bean.NoleggioAutoBean;
import bean.NotificaBean;
import controller.NoleggioController;
import exceptions.DocsNotValidException;
import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.duratacontrattuale.PianoNoleggio;
import model.macchina.Macchina;
import model.utente.Utente;
import utils.SessionSingleton;
import utils.StageHandler;
import utils.ImageUtils;
import view.factory.ControllerFactory;

import java.io.IOException;


public class GuiNoleggioAuto {

    private static String style="Totale: 0,00 €";

    private final NoleggioController noleggioController=ControllerFactory.getGraphicalSingletonFactory().createNoleggioController();


    @FXML
    private ImageView imgAuto;

    @FXML
    private Label modello;

    @FXML
    private Label posti;

    @FXML
    private Label alimentazione;

    @FXML
    private Label prezzo;

    @FXML
    private Label trasmissione;

    @FXML
    private Label anno;

    @FXML
    private Label tipologia;


    @FXML
    public void initialize() {

        loadInfo();

    }

    @FXML
    public void segnala() {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Segnalazione");
        dialog.setHeaderText("Invia una segnalazione");

        ButtonType inviaButtonType = new ButtonType("Invia", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(inviaButtonType, ButtonType.CANCEL);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Descrivi il problema (max 100 caratteri):");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        textArea.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 100 ? change : null
        ));

        Button inviaButton = (Button) dialog.getDialogPane().lookupButton(inviaButtonType);
        inviaButton.setDisable(true);
        textArea.textProperty().addListener((obs, oldVal, newVal) ->
                inviaButton.setDisable(newVal.trim().isEmpty())
        );

        dialog.getDialogPane().setContent(textArea);

        dialog.setResultConverter(dialogButton ->
                dialogButton == inviaButtonType ? textArea.getText() : null
        );

        dialog.showAndWait().ifPresent(testo -> {
            if (!testo.trim().isEmpty()) {

                NotificaBean bean = new NotificaBean();
                bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
                bean.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
                bean.setMsg(testo);

                noleggioController.segnalazione(bean);
            }
        });
    }

    private void loadInfo() {

        Macchina auto = noleggioController.getAuto();

        if (auto == null) {
            throw new GenericSystemException("Errore: Nessuna auto selezionata in sessione.");
        }

        String marca = auto.getMarca();
        String mod =auto.getModello();

        modello.setText(marca + " " + mod);
        prezzo.setText(auto.getPrezzo() > 0 ? "Prezzo al giorno: " + auto.getPrezzo() + " €" : "Prezzo al giorno: N/D");

        posti.setText(checkInt(auto.getPosti()));
        anno.setText(checkInt(auto.getAnno()));

        alimentazione.setText(checkStr(auto.getAlimentazione(), "N/D"));
        trasmissione.setText(checkStr(auto.getTrasmissione(), "N/D"));
        tipologia.setText(checkStr(auto.getTipologia(), "N/D"));

        if (imgAuto != null && checkStr(auto.getImageUrl(), null) != null) {
            imgAuto.setImage(ImageUtils.loadCarImage(auto.getImageUrl()));
        }
    }

    private String checkStr(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private String checkInt(int value) {
        return value > 0 ? String.valueOf(value) : "N/D";
    }

    @FXML
    public void goHome(ActionEvent event) throws IOException {
        String str = "/view/CatalogoView.fxml";
        noleggioController.clear();
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void shop(ActionEvent event) throws IOException {

        NoleggioAutoBean bean = new NoleggioAutoBean();

        bean.setRenter(SessionSingleton.getInstance().getUtenteCorrente());

        if(bean.getRenter()==null){
            String str="/view/login.fxml";
            StageHandler.getSingletonInstance().loadPage(str);
            return;
        }

        bean.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
        openWindow(bean);

    }

    private void openWindow(NoleggioAutoBean acquistoAuto) {

        final String[] metodoScelto = {null};

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Riepilogo Noleggio");

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0E1A33; -fx-padding: 20;");

        VBox mainCard = new VBox(20);
        mainCard.getStyleClass().add("white-card");
        mainCard.setPadding(new Insets(25));
        mainCard.setAlignment(Pos.TOP_CENTER);
        mainCard.setPrefWidth(350);

        Macchina auto = acquistoAuto.getMacchina();

        Label lblTitolo = new Label("CONFIGURA NOLEGGIO");
        lblTitolo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #204080;");

        TextField txtGiorni = new TextField();
        txtGiorni.setPromptText("Inserisci durata (giorni)");
        txtGiorni.setStyle("-fx-background-radius: 10; -fx-pref-height: 35; -fx-alignment: center;");

        VBox scontrinoBox = new VBox(10);
        scontrinoBox.getStyleClass().add("spec-box");
        scontrinoBox.setAlignment(Pos.CENTER_LEFT);

        Label lblScontrinoTitolo = new Label("RIEPILOGO COSTI");
        lblScontrinoTitolo.setStyle("-fx-font-weight: bold; -fx-text-fill: #204080; -fx-font-size: 12px;");

        Label lblAuto = new Label("🚗 " + auto.getMarca() + " " + auto.getModello());
        lblAuto.setStyle("-fx-text-fill: #475569;");

        Label lblDettagliPiano = new Label("Piano: - ");
        lblDettagliPiano.setStyle("-fx-text-fill: #475569;");

        Label lblPrezzoFinale = new Label("Totale: € 0.00");
        lblPrezzoFinale.setStyle("-fx-font-weight: bold; -fx-text-fill: #204080; -fx-font-size: 18px;");

        Label lblErrore = new Label();
        lblErrore.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblErrore.setVisible(false);
        lblErrore.setWrapText(true);

        scontrinoBox.getChildren().addAll(lblScontrinoTitolo, lblAuto, lblDettagliPiano, lblPrezzoFinale);

        txtGiorni.textProperty().addListener((obs, oldText, newText) -> {
            aggiornaScontrinoVisivo(newText, auto, lblDettagliPiano, lblPrezzoFinale);
            lblErrore.setVisible(false);
        });

        Label lblMetodoPagamento = new Label("Scegli il metodo di pagamento");
        lblMetodoPagamento.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");

        Button btnSaldo = new Button("Saldo");
        Button btnStripe = new Button("Stripe");

        String stileBase       = "-fx-border-radius: 8; -fx-background-radius: 8; -fx-pref-height: 38; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand;";
        String stileSaldoNorm  = stileBase + "-fx-background-color: #FFFFFF; -fx-text-fill: #204080; -fx-border-color: #204080; -fx-border-width: 1.5;";
        String stileStripeNorm = stileBase + "-fx-background-color: #204080; -fx-text-fill: #FFFFFF; -fx-border-color: #204080; -fx-border-width: 1.5;";
        String stileSaldoAtt   = stileBase + "-fx-background-color: #FFFFFF; -fx-text-fill: #204080; -fx-border-color: #204080; -fx-border-width: 2.5;";
        String stileStripeAtt  = stileBase + "-fx-background-color: #183060; -fx-text-fill: #FFFFFF; -fx-border-color: #183060; -fx-border-width: 2.5;";

        btnSaldo.setStyle(stileSaldoNorm);
        btnStripe.setStyle(stileStripeNorm);
        btnSaldo.setPrefWidth(145);
        btnStripe.setPrefWidth(145);

        btnSaldo.setOnAction(e -> {
            metodoScelto[0] = "saldo";
            btnSaldo.setStyle(stileSaldoAtt);
            btnStripe.setStyle(stileStripeNorm);
        });

        btnStripe.setOnAction(e -> {
            metodoScelto[0] = "stripe";
            btnSaldo.setStyle(stileSaldoNorm);
            btnStripe.setStyle(stileStripeAtt);
        });

        HBox metodoBox = new HBox(12, btnSaldo, btnStripe);
        metodoBox.setAlignment(Pos.CENTER);

        Button btnConferma = new Button("CONFERMA");
        Button btnAnnulla = new Button("ANNULLA");

        btnConferma.getStyleClass().add("Button");
        btnAnnulla.getStyleClass().add("Button-Secondary");

        double larghezzaBottone = 140;
        btnConferma.setPrefWidth(larghezzaBottone);
        btnAnnulla.setPrefWidth(larghezzaBottone);

        btnConferma.setOnAction(e -> gestisciConferma(txtGiorni.getText(), acquistoAuto, popupStage, lblErrore, metodoScelto[0]));
        btnAnnulla.setOnAction(e -> popupStage.close());

        HBox bottoniBox = new HBox(15, btnConferma, btnAnnulla);
        bottoniBox.setAlignment(Pos.CENTER);

        mainCard.getChildren().addAll(
                lblTitolo,
                new Label("Quanti giorni desideri noleggiare l'auto?"),
                txtGiorni,
                scontrinoBox,
                lblMetodoPagamento,
                metodoBox,
                lblErrore,
                bottoniBox
        );

        root.getChildren().add(mainCard);

        Scene scene = new Scene(root, 400, 560);
        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setScene(scene);
        popupStage.setResizable(false);
        popupStage.showAndWait();
    }

    private void aggiornaScontrinoVisivo(String testoGiorni, Macchina auto, Label lblDettagli, Label lblPrezzo) {
        try {
            if (testoGiorni == null || testoGiorni.trim().isEmpty()) {
                resettaTestoScontrino(lblDettagli, lblPrezzo);
                return;
            }

            int giorni = Integer.parseInt(testoGiorni.trim());
            if (giorni <= 0) return;

            PianoNoleggio pianoScelto = noleggioController.determinaPiano(giorni);
            double totale = noleggioController.calcolaTotale(auto, giorni);

            lblDettagli.setText("Piano: " + pianoScelto.getDescrizione());
            lblPrezzo.setText(String.format("Totale: %.2f €", totale));

        } catch (NumberFormatException ex) {
            lblDettagli.setText("Errore: Inserire un numero valido");
            lblPrezzo.setText(style);
        } catch (IllegalArgumentException ex) {
            lblDettagli.setText(ex.getMessage());
            lblPrezzo.setText("Totale: Non calcolabile");
        }
    }

    private void resettaTestoScontrino(Label lblDettagli, Label lblPrezzo) {
        lblDettagli.setText("Piano: Inserisci i giorni...");
        lblPrezzo.setText(style);
    }

    private void gestisciConferma(String testoGiorni, NoleggioAutoBean bean, Stage stage, Label lblErrore, String metodo) {
        try {
            if (testoGiorni == null || testoGiorni.trim().isEmpty()) {
                lblErrore.setText("Inserire i giorni del noleggio.");
                lblErrore.setVisible(true);
                return;
            }
            if (metodo == null) {
                lblErrore.setText("Seleziona un metodo di pagamento.");
                lblErrore.setVisible(true);
                return;
            }

            int giorni = Integer.parseInt(testoGiorni.trim());
            if (giorni <= 0) throw new NumberFormatException();

            bean.setGiorni(giorni);
            bean.setMetodoPagamento(metodo);

            if ("stripe".equals(metodo)) {

                stage.close();

                Macchina autoSnapshot = bean.getMacchina();
                Utente utenteSnapshot = bean.getRenter();
                String modello1 = autoSnapshot.getModello();
                String marca1 = autoSnapshot.getMarca();

                new Thread(() -> {
                    try {

                        noleggioController.processaNoleggioStripe(bean);
                        NotificaBean notifica = new NotificaBean();
                        notifica.setUtente(utenteSnapshot);
                        notifica.setMacchina(autoSnapshot);
                        notifica.setMsg("Noleggio di " + modello1 + " " + marca1 + " effettuato con successo via Stripe!");
                        noleggioController.notificaSistema(notifica);

                        javafx.application.Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Pagamento completato");
                            alert.setHeaderText("Noleggio confermato!");
                            alert.setContentText("Il noleggio di " + modello1 + " " + marca1 + " è stato effettuato con successo via Stripe.");
                            alert.showAndWait();
                            stage.close();
                        });

                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();

                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Pagamento Stripe");
                            alert.setHeaderText("Errore durante il pagamento");
                            alert.setContentText(ex.getMessage());
                            alert.showAndWait();
                        });
                    }
                }).start();

            } else {

                noleggioController.processaNoleggio(bean);

                NotificaBean notifica = new NotificaBean();

                notifica.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
                notifica.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
                notifica.setMsg("Noleggio di " + notifica.getMacchina().getModello() + " " + notifica.getMacchina().getMarca() + " effettuato con successo!");

                noleggioController.notificaSistema(notifica);
                stage.close();
                StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");
            }

        } catch (NumberFormatException ex) {
            lblErrore.setText("Formato giorni non valido.");
            lblErrore.setVisible(true);
        } catch (IllegalArgumentException | DocsNotValidException | IOException ex) {
            lblErrore.setText(ex.getMessage());
            lblErrore.setVisible(true);
        }
    }
}