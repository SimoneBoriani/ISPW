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

        scontrinoBox.getChildren().addAll(lblScontrinoTitolo, lblAuto, lblDettagliPiano, lblPrezzoFinale);

        txtGiorni.textProperty().addListener((obs, oldText, newText) ->
                aggiornaScontrinoVisivo(newText, auto, lblDettagliPiano, lblPrezzoFinale)
        );

        Button btnConferma = new Button("CONFERMA");
        Button btnAnnulla = new Button("ANNULLA");

        btnConferma.getStyleClass().add("Button");
        btnAnnulla.getStyleClass().add("Button-Secondary");

        btnConferma.setPrefWidth(140);
        btnAnnulla.setPrefWidth(140);

        btnConferma.setOnAction(e -> gestisciConferma(txtGiorni.getText(), acquistoAuto, popupStage));
        btnAnnulla.setOnAction(e -> popupStage.close());

        HBox bottoniBox = new HBox(15, btnConferma, btnAnnulla);
        bottoniBox.setAlignment(Pos.CENTER);

        mainCard.getChildren().addAll(lblTitolo, new Label("Quanti giorni desideri noleggiare l'auto?"), txtGiorni, scontrinoBox, bottoniBox);
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