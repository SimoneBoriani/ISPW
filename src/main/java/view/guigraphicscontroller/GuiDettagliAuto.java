package view.guigraphicscontroller;

import controller.MainPageCatalogoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.macchina.Macchina;
import utils.StageHandler;

import java.io.IOException;

public class GuiDettagliAuto extends MainPageCatalogoController {

    @FXML
    private ImageView imgAuto;
    @FXML
    private Label modello;
    @FXML
    private Label km;
    @FXML
    private Label posti;
    @FXML
    private Label alimentazione;
    @FXML
    private Label prezzo;

    @FXML
    public void initialize() {

        Macchina autoSelezionata = getAutoSelezionataDaSessione();


        if (autoSelezionata != null) {

            modello.setText(autoSelezionata.getCasa()+" "+autoSelezionata.getModello());
            km.setText(String.valueOf(autoSelezionata.getKm()));
            posti.setText(String.valueOf(autoSelezionata.getPosti()));
            alimentazione.setText(autoSelezionata.getAlimentazione());
            prezzo.setText(autoSelezionata.getPrezzo()+" €");

        }

        if (imgAuto != null) {
            imgAuto.setImage(utils.ImageUtils.loadCarImage(autoSelezionata.getImageUrl()));
        }

        //pulisciSelezioneSessione();

    }

    @FXML
    public void goHome(ActionEvent event) throws IOException {
        String str="/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }
}
