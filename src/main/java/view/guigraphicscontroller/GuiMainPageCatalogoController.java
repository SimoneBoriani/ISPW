package view.guigraphicscontroller;

import app.SessionSingleton;
import controller.MainPageCatalogoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.macchina.Macchina;
import utils.StageHandler;

import java.io.IOException;
import java.util.List;

public class GuiMainPageCatalogoController extends MainPageCatalogoController{

    @FXML
    private Button btnAddCar;

    @FXML
    private Button btnAccedi;

    @FXML
    private Button btnLogout;

    @FXML
    private TableView<Macchina> tblMacchinas;

    @FXML
    private TableView<Macchina> tableView;

    @FXML private TableColumn<Macchina, String> nome;
    @FXML private TableColumn<Macchina, String> casa;
    @FXML private TableColumn<Macchina, Integer> km;
    @FXML private TableColumn<Macchina, Integer> prezzo;
    @FXML private TableColumn<Macchina, String> tipologia;


    @FXML
    public void initialize() {

        nome.setCellValueFactory(new PropertyValueFactory<>("modello"));
        casa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        km.setCellValueFactory(new PropertyValueFactory<>("km"));
        prezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        tipologia.setCellValueFactory(new PropertyValueFactory<>("tipologia"));

        List<Macchina> listaAuto = getCars();

        if (listaAuto != null) {
            ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
            tableView.setItems(data);
        }

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
    public void btnAccediOnAction(ActionEvent event) throws IOException {

        String str= "/view/Login.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void btnLogoutOnAction(ActionEvent event) throws IOException {

        SessionSingleton.getInstance().logout();
        StageHandler.getSingletonInstance().loadPage("/view/PrincipalPage-Catalogo.fxml");

    }
}