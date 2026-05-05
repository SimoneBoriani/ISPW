package view.guigraphicscontroller;

import bean.AcquistoAutoBean;
import controller.GestioneCatalogoController;
import controller.MainPageCatalogoController;
import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.macchina.Macchina;
import utils.SessionSingleton;
import utils.StageHandler;
import utils.ImageUtils;
import view.factory.ControllerFactory;

import java.io.IOException;


public class GuiDettagliAuto {


    private final MainPageCatalogoController controllerApplicativo= ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();
    private final GestioneCatalogoController gestioneCatalogoController=ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    @FXML private ImageView imgAuto;
    @FXML private Label modello;
    @FXML private Label km;
    @FXML private Label posti;
    @FXML private Label alimentazione;
    @FXML private Label prezzo;
    @FXML private Button btnModifica;

    @FXML
    public void initialize() {

        loadInfo();
        buttonLogic();

    }

    private void buttonLogic() {

        btnModifica.setVisible(false);
        btnModifica.setManaged(false);

        if(SessionSingleton.getInstance().getUtenteCorrente() != null ) {
            if (SessionSingleton.getInstance().getUtenteCorrente().getRuolo().equals("ADMIN")) {
                btnModifica.setVisible(true);
                btnModifica.setManaged(true);
            }
        }
    }

    private void loadInfo(){
        Macchina autoSelezionata = controllerApplicativo.getAutoSelezionataDaSessione();

            if (autoSelezionata != null) {

            modello.setText(autoSelezionata.getCasa() + " " + autoSelezionata.getModello());
            km.setText(String.valueOf(autoSelezionata.getKm()));
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
    public void goManage(ActionEvent event) throws IOException {
        String str = "/view/OpzioniAuto.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void shop(ActionEvent event) throws IOException {

        AcquistoAutoBean pallenegre = new AcquistoAutoBean(); //USARE VECCHI BEAN -> COSTRUIRE NEL CONTROLLER

        pallenegre.setBuyer(SessionSingleton.getInstance().getUtenteCorrente());

        if(pallenegre.getBuyer()==null){
            String str="/view/login.fxml";
            StageHandler.getSingletonInstance().loadPage(str);
            return;
        }

        pallenegre.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
        openWindow(pallenegre);

    }

    private void openWindow(AcquistoAutoBean acquistoAuto) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Conferma Acquisto");


        Button btnConferma = new Button("Conferma");
        Button btnCancela = new Button("Cancela");

        btnConferma.getStyleClass().add("Button");
        btnCancela.getStyleClass().add("Button");

        btnConferma.setOnAction(e -> {
            gestioneCatalogoController.buyRequest(acquistoAuto);
            popupStage.close();
        });
        btnCancela.setOnAction(e -> {
            popupStage.close();
        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        layoutPopup.getChildren().addAll(
                new Label("Conferma acquisto:"),
                btnConferma,
                btnCancela
        );

        Scene scene = new Scene(layoutPopup, 300, 350);

        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setScene(scene);
        popupStage.showAndWait();

    }
}