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

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Segnalazione");
        dialog.setHeaderText("Invia una segnalazione");
        dialog.setContentText("Descrivi il problema (max 100 caratteri):");


        TextField textField = dialog.getEditor();
        textField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 100 ? change : null
        ));

        dialog.showAndWait().ifPresent(testo -> {
            if (testo != null && !testo.trim().isEmpty()) {

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

        txtGiorni.textProperty().addListener((obs, oldText, newText) ->{
            aggiornaScontrinoVisivo(newText, auto, lblDettagliPiano, lblPrezzoFinale);
            lblErrore.setVisible(false);}
        );

        Button btnConferma = new Button("CONFERMA");
        Button btnAnnulla = new Button("ANNULLA");

        btnConferma.getStyleClass().add("Button");
        btnAnnulla.getStyleClass().add("Button-Secondary");

        double larghezzaBottone = 140;
        btnConferma.setPrefWidth(larghezzaBottone);
        btnAnnulla.setPrefWidth(larghezzaBottone);

        btnConferma.setOnAction(e -> gestisciConferma(txtGiorni.getText(), acquistoAuto, popupStage, lblErrore));

        btnAnnulla.setOnAction(e -> popupStage.close());

        HBox bottoniBox = new HBox(15, btnConferma, btnAnnulla);
        bottoniBox.setAlignment(Pos.CENTER);

        mainCard.getChildren().addAll(lblTitolo, new Label("Quanti giorni desideri noleggiare l'auto?"), txtGiorni, scontrinoBox, lblErrore, bottoniBox);
        root.getChildren().add(mainCard);

        Scene scene = new Scene(root, 400, 480);
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

    private void gestisciConferma(String testoGiorni, NoleggioAutoBean bean, Stage stage, Label lblErrore) {
        try {
            if (testoGiorni == null || testoGiorni.trim().isEmpty()) {
                lblErrore.setText("Inserire i giorni del noleggio.");
                lblErrore.setVisible(true);
                return;
            }

            bean.setGiorni(Integer.parseInt(testoGiorni.trim()));
            if (bean.getGiorni() <= 0) throw new NumberFormatException();

            noleggioController.processaNoleggio(bean);

            NotificaBean bean1= new NotificaBean();
            bean1.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
            bean1.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
            bean1.setMsg("Noleggio di " + bean1.getMacchina().getModello() + " " + bean1.getMacchina().getMarca() + " effettua con successo!");

            noleggioController.notificaSistema(bean1);
            stage.close();

        } catch (NumberFormatException ex) {
            lblErrore.setText("Formato giorni non valido.");
            lblErrore.setVisible(true);
        } catch (IllegalArgumentException | DocsNotValidException ex) {
            lblErrore.setText(ex.getMessage());
            lblErrore.setVisible(true);
        }
    }
}