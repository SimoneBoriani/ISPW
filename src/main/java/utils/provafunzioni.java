package utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

import java.io.IOException;

public class provafunzioni {
    @FXML private TextField model,brand,prezzo,alimentazione,km,posti;
    @FXML private Button delete,update;

    @FXML
    public void backHome(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");
    }

    @FXML
    public void delete(ActionEvent event) throws IOException {



        Macchina palle1 = SessionSingleton.getInstance().getAutoSelezionata();
        DaoFactory.getDaoSingletonFactory().createMacchinaDao().remove(palle1.getId());

    }

    @FXML
    public void modificaCar(ActionEvent event) throws IOException {

        String model1=model.getText().trim();
        String brand1=brand.getText().trim();
        String prezzo1=prezzo.getText().trim();
        String alimentazione1=alimentazione.getText().trim();
        String km1=km.getText().trim();
        String posti1=posti.getText().trim();

        SessionSingleton.getInstance().getAutoSelezionata().setAlimentazione(alimentazione1);
        SessionSingleton.getInstance().getAutoSelezionata().setCasa(brand1);
        SessionSingleton.getInstance().getAutoSelezionata().setModello(model1);
        SessionSingleton.getInstance().getAutoSelezionata().setPrezzo(Integer.parseInt(prezzo1));
        SessionSingleton.getInstance().getAutoSelezionata().setKm(Integer.parseInt(km1));
        SessionSingleton.getInstance().getAutoSelezionata().setPosti(Integer.parseInt(posti1));

        DaoFactory.getDaoSingletonFactory().createMacchinaDao().update(SessionSingleton.getInstance().getAutoSelezionata());
    }
}
