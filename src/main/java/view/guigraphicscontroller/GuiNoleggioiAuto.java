package view.guigraphicscontroller;

import bean.NoleggioAutoBean;
import controller.NoleggioController;
import controller.VisualizzaCatalogoController;
import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.duratacontrattuale.PianoNoleggio;
import model.macchina.Macchina;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SessionSingleton;
import utils.StageHandler;
import utils.ImageUtils;
import view.factory.ControllerFactory;

import java.io.IOException;



public class GuiNoleggioiAuto {

    private final Logger logger= LogManager.getLogger(GuiNoleggioiAuto.class);
    private static String style="Totale: 0,00 €";

    private final VisualizzaCatalogoController controllerApplicativo= ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();
    private final NoleggioController noleggioController=ControllerFactory.getGraphicalSingletonFactory().createNoleggioController();

    @FXML private ImageView imgAuto;
    @FXML private Label modello;
    @FXML private Label posti;
    @FXML private Label alimentazione;
    @FXML private Label prezzo;

    @FXML
    public void initialize() {

        loadInfo();

    }

    private void loadInfo(){
        Macchina autoSelezionata = controllerApplicativo.getAutoSelezionataDaSessione();

            if (autoSelezionata != null) {

            modello.setText(autoSelezionata.getMarca() + " " + autoSelezionata.getModello());
            posti.setText(String.valueOf(autoSelezionata.getPosti()));
            alimentazione.setText(autoSelezionata.getAlimentazione());
            prezzo.setText(autoSelezionata.getPrezzo() + " €");

            if (imgAuto != null) {
                imgAuto.setImage(ImageUtils.loadCarImage(autoSelezionata.getImageUrl()));
            }
            } else {
                throw new GenericSystemException("Errore: Nessuna auto selezionata in sessione.");
            }
        }

    @FXML
    public void goHome(ActionEvent event) throws IOException {
        String str = "/view/CatalogoView.fxml";
        controllerApplicativo.pulisciSelezioneSessione();
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

        Macchina auto = acquistoAuto.getMacchina();
        TextField txtGiorni = new TextField();
        txtGiorni.setPromptText("Numero di giorni (es. 3)");

        Label lblScontrinoTitolo = new Label("--- SCONTRINO ---");
        lblScontrinoTitolo.setStyle("-fx-font-weight: bold;");
        Label lblAuto = new Label("Auto: " + auto.getMarca() + " " + auto.getModello());
        Label lblDettagliPiano = new Label("Piano: Inserisci i giorni...");
        Label lblPrezzoFinale = new Label(style);
        lblPrezzoFinale.setStyle("-fx-font-weight: bold; -fx-text-fill: green; -fx-font-size: 16px;");

        VBox scontrinoBox = new VBox(5, lblScontrinoTitolo, lblAuto, lblDettagliPiano, lblPrezzoFinale);
        scontrinoBox.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #f9f9f9;");

        txtGiorni.textProperty().addListener((obs, oldText, newText) ->
                aggiornaScontrinoVisivo(newText, auto, lblDettagliPiano, lblPrezzoFinale)
        );

        Button btnConferma = new Button("Conferma");
        Button btnAnnulla = new Button("Annulla");

        btnConferma.getStyleClass().add("Button");
        btnAnnulla.getStyleClass().add("Button");

        btnConferma.setOnAction(e -> gestisciConferma(txtGiorni.getText(), acquistoAuto, popupStage));
        btnAnnulla.setOnAction(e -> popupStage.close());

        HBox bottoniBox = new HBox(15, btnConferma, btnAnnulla);
        bottoniBox.setAlignment(Pos.CENTER);

        VBox layoutPopup = new VBox(15, new Label("Inserisci i giorni di noleggio:"), txtGiorni, scontrinoBox, bottoniBox);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layoutPopup, 350, 400);
        StageHandler.getSingletonInstance().loadCss(scene);
        popupStage.setScene(scene);
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

    private void gestisciConferma(String testoGiorni, NoleggioAutoBean bean, Stage stage) {
        try {
            if (testoGiorni == null || testoGiorni.trim().isEmpty()) {
                if (logger != null) logger.error("Inserisci i giorni del noleggio.");
                return;
            }

            int giorni = Integer.parseInt(testoGiorni.trim());
            if (giorni <= 0) throw new NumberFormatException();

            noleggioController.processaNoleggio(bean, giorni);
            stage.close();

        } catch (NumberFormatException ex) {
            logger.error("Formato giorni non valido.");
        } catch (Exception ex) {
            logger.error("Errore durante il noleggio");
            }
        }

}