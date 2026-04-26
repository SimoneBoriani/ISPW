package view.guigraphicscontroller;

import exceptions.GenericSystemException;
import utils.SessionSingleton;
import controller.MainPageCatalogoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.macchina.Macchina;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.util.List;


public class GuiMainPageCatalogoController{

    private final MainPageCatalogoController mainPageCatalogoController= ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();

    @FXML
    private Button btnAddCar;

    @FXML
    private Button btnAccedi;

    @FXML
    private Button btnLogout;

    @FXML
    private ListView<Macchina> carListView;

    @FXML
    public void initialize() {

        configuraCatalogo();
        configuraBottoni();

    }

    private void apriDettaglio(Macchina auto) {
        try {

            SessionSingleton.getInstance().setAutoSelezionata(auto);
            StageHandler.getSingletonInstance().loadPage("/view/CarDetailsView.fxml");

        } catch (IOException e) {
            throw new GenericSystemException("Errore nel caricamento della pagina DettaglioAuto: ",e);
        }
    }

    private void configuraCatalogo() {
        try {
            if (carListView != null) {
                List<Macchina> listaAuto = mainPageCatalogoController.getCars();

                if (listaAuto != null && !listaAuto.isEmpty()) {
                    ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
                    carListView.setItems(data);
                    carListView.setCellFactory(param -> new CarCell());
                }

                carListView.setOnMouseClicked(event -> {
                    Macchina selezionata = carListView.getSelectionModel().getSelectedItem();
                    if (selezionata != null) {
                        apriDettaglio(selezionata);
                    }
                });
            }
        } catch (Exception e) {
            throw new GenericSystemException("Errore Caricamento", e);
        }
    }

    private void configuraBottoni() {
        boolean loggedIn = SessionSingleton.getInstance().isUserLoggedIn();
        btnAddCar.setVisible(false);
        btnAddCar.setManaged(false);

        if (loggedIn) {
            btnAccedi.setVisible(false);
            btnAccedi.setManaged(false);
            if (btnLogout != null) {
                btnLogout.setVisible(true);
                btnLogout.setManaged(true);
                if(SessionSingleton.getInstance().getUtenteCorrente().getRuolo().equals("ADMIN")){
                    btnAddCar.setVisible(true);
                    btnAddCar.setManaged(true);
                }
            }
        } else {
            btnAccedi.setVisible(true);
            btnAccedi.setManaged(true);
            if (btnLogout != null) {
                btnLogout.setVisible(false);
                btnLogout.setManaged(false);
            }
        }
    }

    @FXML
    public void btnAddCarOnAction(ActionEvent event) throws IOException {
        String str="/view/AggiungiAuto.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void btnAccediOnAction(ActionEvent event) throws IOException {

        String str= "/view/Login.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void btnLogoutOnAction(ActionEvent event) throws IOException {

        SessionSingleton.getInstance().logout();
        StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");

    }
    @FXML
    public void goToTest(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/OpzioniAuto.fxml");
    }
}