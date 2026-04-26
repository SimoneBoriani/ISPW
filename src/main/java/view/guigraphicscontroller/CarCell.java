package view.guigraphicscontroller;

import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.macchina.Macchina;
import utils.StageHandler;
import utils.ImageUtils;
import utils.SessionSingleton; // Aggiungi il tuo import

import java.io.IOException;

public class CarCell extends ListCell<Macchina> {

    @FXML private HBox rootAnchor;
    @FXML private Label lblModello;
    @FXML private Label lblPrezzo;
    @FXML private Label lblDettagli;
    @FXML private ImageView imgAuto;

    private FXMLLoader loader;

    @Override
    protected void updateItem(Macchina macchina, boolean empty) {
        super.updateItem(macchina, empty);

        if (empty || macchina == null) {
            setGraphic(null);
            setText(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/view/CarCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    throw new GenericSystemException("Errore caricamento cella", e);
                }
            }

            if (imgAuto != null) {
                imgAuto.setImage(ImageUtils.loadCarImage(macchina.getImageUrl()));
            }

            lblModello.setText(macchina.getCasa() + " " + macchina.getModello());
            lblPrezzo.setText(macchina.getPrezzo() + " €");
            lblDettagli.setText(macchina.getKm() + " km • " + macchina.getAnno() + " • " + macchina.getAlimentazione());

            setGraphic(rootAnchor);
            setText(null);
        }
    }

    @FXML
    public void managePage(ActionEvent event) throws IOException {

        Macchina autoCliccata = getItem();
        if (autoCliccata != null) {
            SessionSingleton.getInstance().setAutoSelezionata(autoCliccata);
        }

        String str = "/view/provafunzioni.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }
}