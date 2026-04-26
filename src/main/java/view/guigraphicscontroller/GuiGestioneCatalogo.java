package view.guigraphicscontroller;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import exceptions.GenericSystemException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;

public class GuiGestioneCatalogo{

    private final GestioneCatalogoController gestioneCatalogoController= ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    @FXML
    private TextField model;
    @FXML
    private TextField brand;
    @FXML
    private TextField alimentazione;
    @FXML
    private TextField posti;
    @FXML
    private TextField km;
    @FXML
    private TextField prezzo;
    @FXML
    private TextField urlFoto;

    @FXML private Button delete;
    @FXML private Button update;

    @FXML
    public void backHome(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");
    }

    @FXML
    public void delete(ActionEvent event){

        CatalogoBean catalogoBean=new CatalogoBean();
        catalogoBean.setId(SessionSingleton.getInstance().getAutoSelezionata().getId());

        gestioneCatalogoController.removeCar(catalogoBean);

    }

    @FXML
    public void modificaCar(ActionEvent event){

        CatalogoBean catalogo = new CatalogoBean();
        catalogo.setId(SessionSingleton.getInstance().getAutoSelezionata().getId());

        String model1 = model.getText().trim();
        if (!model1.isEmpty()) {
            catalogo.setModello(model1);
        }

        String brand1 = brand.getText().trim();
        if (!brand1.isEmpty()) {
            catalogo.setMarca(brand1);
        }

        String alimentazione1 = alimentazione.getText().trim();
        if (!alimentazione1.isEmpty()) {
            catalogo.setAlimentazione(alimentazione1);
        }

        try {
            String prezzo1 = prezzo.getText().trim();
            if (!prezzo1.isEmpty()) {
                catalogo.setPrezzo(Integer.parseInt(prezzo1));
            }

            String km1 = km.getText().trim();
            if (!km1.isEmpty()) {
                catalogo.setKm(Integer.parseInt(km1));
            }

            String posti1 = posti.getText().trim();
            if (!posti1.isEmpty()) {
                catalogo.setPosti(Integer.parseInt(posti1));
            }

            String urlFoto1 = urlFoto.getText().trim();
            if (!urlFoto1.isEmpty()) {
                catalogo.setFoto(urlFoto1);
            }

            gestioneCatalogoController.modifyCar(catalogo);

        } catch (NumberFormatException e) {
            throw new GenericSystemException("Errore di compilazione: Prezzo, Km e Posti devono contenere SOLO numeri interi.");
        }
    }
}