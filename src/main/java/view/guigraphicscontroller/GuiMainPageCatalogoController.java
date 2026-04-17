package view.guigraphicscontroller;

import utils.SessionSingleton;
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

    @FXML private ListView<Macchina> carListView;

    @FXML
    private TableView<Macchina> tableView;

    @FXML private TableColumn<Macchina, String> nome;
    @FXML private TableColumn<Macchina, String> casa;
    @FXML private TableColumn<Macchina, Integer> km;
    @FXML private TableColumn<Macchina, Integer> prezzo;
    @FXML private TableColumn<Macchina, String> tipologia;


    @FXML
    public void initialize() {

       /* nome.setCellValueFactory(new PropertyValueFactory<>("modello"));
        casa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        km.setCellValueFactory(new PropertyValueFactory<>("km"));
        prezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        tipologia.setCellValueFactory(new PropertyValueFactory<>("tipologia"));

        List<Macchina> listaAuto = getCars();

        if (listaAuto != null) {
            ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
            tableView.setItems(data);
        }*/
        List<Macchina> listaAuto = getCars();

        if (listaAuto != null) {
            ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
            carListView.setItems(data);

            // 2. Opzionale: Definisci come apparirà ogni riga (CellFactory)
            // Se non metti questo, vedrai il risultato del metodo .toString() di Macchina
            carListView.setCellFactory(param -> new ListCell<Macchina>() {
                @Override
                protected void updateItem(Macchina item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Personalizza qui cosa vuoi leggere nella riga
                        setText(item.getCasa() + " " + item.getModello() + " - €" + item.getPrezzo());
                    }
                }
            });
        }

        configuraBottoni();
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
        StageHandler.getSingletonInstance().loadPage("/view/PrincipalPage-Catalogo.fxml");

    }
}