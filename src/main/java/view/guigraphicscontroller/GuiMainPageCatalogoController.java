package view.guigraphicscontroller;

import bean.CatalogoBean;
import exceptions.GenericSystemException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(GuiMainPageCatalogoController.class.getName());

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
    public void goToProfile(MouseEvent event) throws IOException{
        String str="/view/Profilo.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void goToGarage(MouseEvent event) throws IOException{
        String str="/view/Garage.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void refresh(MouseEvent event){
        configuraCatalogo();
    }

    @FXML
    public void research(MouseEvent event){
        apriPopupFiltro();
    }

    private void apriPopupFiltro() {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Ricerca auto");

        TextField txtMarca = new TextField();
        txtMarca.setPromptText("Marca");

        TextField txtModello = new TextField();
        txtModello.setPromptText("Modello");

        TextField txtPrezzo = new TextField();
        txtPrezzo.setPromptText("Prezzo");

        TextField txtKm = new TextField();
        txtKm.setPromptText("Km)");

        TextField txtAlimentazione = new TextField();
        txtAlimentazione.setPromptText("Alimentazione");

        Button btnAvviaRicerca = new Button("Avvia Ricerca");

        btnAvviaRicerca.getStyleClass().add("Button");

        btnAvviaRicerca.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            String marca = txtMarca.getText().trim();
            if (!marca.isEmpty()) {
                bean.setMarca(marca);
            }

            String modello = txtModello.getText().trim();
            if (!modello.isEmpty()) {
                bean.setModello(modello);
            }

            String prezzoStr = txtPrezzo.getText().trim();
            if (!prezzoStr.isEmpty()) {
                try {
                    bean.setPrezzo(Integer.parseInt(prezzoStr));
                } catch (NumberFormatException ex) {
                    logger.error("Errore: Inserisci solo numeri nel Prezzo.");
                    return;
                }
            }

            String kmStr = txtKm.getText().trim();
            if (!kmStr.isEmpty()) {
                try {
                    bean.setKm(Integer.parseInt(kmStr));
                } catch (NumberFormatException ex) {
                    logger.error("Errore: Inserisci solo numeri per i km.");
                    return;
                }

            }
            try {

                List<Macchina> autoTrovate = mainPageCatalogoController.research(bean);

                if (carListView != null) {
                    if (autoTrovate != null) {

                        ObservableList<Macchina> data = FXCollections.observableArrayList(autoTrovate);
                        carListView.setItems(data);
                        carListView.setCellFactory(param -> new CarCell());

                    }
                }
            } catch (Exception ex) {
                throw new GenericSystemException("Errore Ricerca: ", ex);
            }

            popupStage.close();

        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        layoutPopup.getChildren().addAll(
                new Label("Filtri di ricerca:"),
                txtMarca,
                txtModello,
                txtPrezzo,
                txtKm,
                txtAlimentazione,
                btnAvviaRicerca
        );

        Scene scene = new Scene(layoutPopup, 300, 350);

        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setScene(scene);
        popupStage.showAndWait();

    }
}