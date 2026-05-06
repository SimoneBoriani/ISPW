package view.guigraphicscontroller;

import bean.NoleggioAutoBean;
import controller.GestioneCatalogoController;
import controller.VisualizzaCatalogoController;
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


    private final VisualizzaCatalogoController controllerApplicativo= ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();
    private final GestioneCatalogoController gestioneCatalogoController=ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

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
    public void goManage(ActionEvent event) throws IOException {
        String str = "/view/OpzioniAuto.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void shop(ActionEvent event) throws IOException {

        NoleggioAutoBean pallenegre = new NoleggioAutoBean(); //USARE VECCHI BEAN -> COSTRUIRE NEL CONTROLLER

        pallenegre.setRenter(SessionSingleton.getInstance().getUtenteCorrente());

        if(pallenegre.getRenter()==null){
            String str="/view/login.fxml";
            StageHandler.getSingletonInstance().loadPage(str);
            return;
        }

        pallenegre.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
        openWindow(pallenegre);

    }

    private void openWindow(NoleggioAutoBean acquistoAuto) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Conferma Acquisto");


        Button btnConferma = new Button("Conferma");
        Button btnCancela = new Button("Cancela");

        btnConferma.getStyleClass().add("Button");
        btnCancela.getStyleClass().add("Button");

        btnConferma.setOnAction(e -> {
            gestioneCatalogoController.rentRequest(acquistoAuto);
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