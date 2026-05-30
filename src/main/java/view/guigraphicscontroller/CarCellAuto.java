package view.guigraphicscontroller;

import exceptions.GenericSystemException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.macchina.Macchina;
import utils.ImageUtils;

import java.io.IOException;

public class CarCellAuto extends ListCell<Macchina> {

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

            lblModello.setText(macchina.getMarca() + " " + macchina.getModello());
            lblPrezzo.setText(macchina.getPrezzo() + " €");
            lblDettagli.setText(macchina.getAnno() + " • " + macchina.getAlimentazione());
            lblNoleggio.setVisible(false);
            lblNoleggio.setManaged(false);

            setGraphic(rootAnchor);
            setText(null);
        }
    }
}