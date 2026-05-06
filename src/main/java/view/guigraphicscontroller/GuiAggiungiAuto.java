package view.guigraphicscontroller;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;

public class GuiAggiungiAuto{

    private GestioneCatalogoController gestioneCatalogoController=ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    @FXML
    private TextField carYear;
    @FXML
    private TextField carName;
    @FXML
    private TextField carPrice;
    @FXML
    private TextField carType;
    @FXML
    private TextField carAlimentation;
    @FXML
    private TextField carBrand;
    @FXML
    private TextField carSeat;
    @FXML
    private TextField carOwners;
    @FXML
    private TextField carKm;

    @FXML
    public void add(ActionEvent event){

        String carYearStr = carYear.getText();
        String carNameStr = carName.getText();
        String carPriceStr = carPrice.getText();
        String carAlimentationStr = carAlimentation.getText();
        String carBrandStr = carBrand.getText();
        String carSeatStr = carSeat.getText();
        String carTypeStr = carType.getText();

        CatalogoBean aggiungiAutoBean = new CatalogoBean();

        aggiungiAutoBean.setAlimentazione(carAlimentationStr);
        aggiungiAutoBean.setMarca(carBrandStr);
        aggiungiAutoBean.setModello(carNameStr);
        aggiungiAutoBean.setTipologia(carTypeStr);
        aggiungiAutoBean.setPosti(Integer.parseInt(carSeatStr));
        aggiungiAutoBean.setPrezzo(Integer.parseInt(carPriceStr));
        aggiungiAutoBean.setAnno(Integer.parseInt(carYearStr));

        gestioneCatalogoController.salvaAutoRam(aggiungiAutoBean);

        carYear.clear();
        carName.clear();
        carPrice.clear();
        carAlimentation.clear();
        carBrand.clear();
        carSeat.clear();
        carOwners.clear();
        carKm.clear();
        carType.clear();

    }
    @FXML
    public void btnHome(ActionEvent event) throws IOException {
        String str= "/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }
}
