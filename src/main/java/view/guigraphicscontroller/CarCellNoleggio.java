package view.guigraphicscontroller;

import exceptions.GenericSystemException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import utils.ImageUtils;

import java.io.IOException;

public class CarCellNoleggio extends ListCell<NoleggioAuto> {

    @FXML
    private HBox rootAnchor;

    @FXML
    private Label lblModello;

    @FXML
    private Label lblPrezzo;

    @FXML
    private Label lblDettagli;

    @FXML
    private Label lblNoleggio;

    @FXML
    private ImageView imgAuto;

    private FXMLLoader loader;

    @Override
    protected void updateItem(NoleggioAuto noleggio, boolean empty) {

        super.updateItem(noleggio, empty);

        if (empty || noleggio == null) {
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

            Macchina macchina = noleggio.getMacchina();

            if (imgAuto != null) {
                imgAuto.setImage(ImageUtils.loadCarImage(macchina.getImageUrl()));
            }

            lblModello.setText(macchina.getMarca() + " " + macchina.getModello());
            lblPrezzo.setText("TOTALE PAGATO: "+noleggio.getPrezzoTotalePagato() + " €");
            lblDettagli.setText(macchina.getAnno() + " • " + macchina.getAlimentazione());
            lblNoleggio.setText("INIZIO: " + noleggio.getDataInizio() + " - FINE: " + noleggio.getDataFine());

            setGraphic(rootAnchor);
            setText(null);
        }
    }
}