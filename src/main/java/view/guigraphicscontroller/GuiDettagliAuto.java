package view.guigraphicscontroller;

import controller.MainPageCatalogoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.macchina.Macchina;
import utils.StageHandler;
import utils.ImageUtils;
import view.factory.ControllerFactory;

import java.io.IOException;

public class GuiDettagliAuto {


    private final MainPageCatalogoController controllerApplicativo= ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();

    @FXML private ImageView imgAuto;
    @FXML private Label modello;
    @FXML private Label km;
    @FXML private Label posti;
    @FXML private Label alimentazione;
    @FXML private Label prezzo;

    @FXML
    public void initialize() {

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
            System.err.println("Errore: Nessuna auto selezionata in sessione.");
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
}