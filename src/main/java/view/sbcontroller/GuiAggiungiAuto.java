package view.sbcontroller;

import bean.AggiungiAutoBean;
import controller.AggiungiAutoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import view.factory.ControllerFactory;

public class GuiAggiungiAuto extends GuiPageManager {

    private AggiungiAutoController controller= ControllerFactory.getInstance().CreateAggiungiAutoController();

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
        String carOwnersStr = carOwners.getText();
        String carKmStr = carKm.getText();
        String carTypeStr = carType.getText();

        AggiungiAutoBean aggiungiAutoBean = new AggiungiAutoBean();

        aggiungiAutoBean.setCarAlimentation(carAlimentationStr);
        aggiungiAutoBean.setCarBrand(carBrandStr);
        aggiungiAutoBean.setCarName(carNameStr);
        aggiungiAutoBean.setCarType(carTypeStr);
        aggiungiAutoBean.setCarSeat(Integer.parseInt(carSeatStr));
        aggiungiAutoBean.setCarKm(Integer.parseInt(carKmStr));
        aggiungiAutoBean.setCarPrice(Integer.parseInt(carPriceStr));
        aggiungiAutoBean.setCarYear(Integer.parseInt(carYearStr));
        aggiungiAutoBean.setCarOwners(Integer.parseInt(carOwnersStr));

        controller.addCar(aggiungiAutoBean);

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
}
