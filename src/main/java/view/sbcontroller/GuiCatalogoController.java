package view.sbcontroller;

import controller.CatalogoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.macchina.Macchina;
import view.factory.ControllerFactory;

import java.util.List;

public class GuiCatalogoController extends GuiPageManager{

    private CatalogoController catalogoController= ControllerFactory.getInstance().CreateCatalogoController();

    @FXML
    private TableView<Macchina> tableView;

    @FXML private TableColumn<Macchina, String> nome;
    @FXML private TableColumn<Macchina, String> casa;
    @FXML private TableColumn<Macchina, Integer> km;
    @FXML private TableColumn<Macchina, Integer> posti;
    @FXML private TableColumn<Macchina, Integer> proprietari;
    @FXML private TableColumn<Macchina, Integer> anno;
    @FXML private TableColumn<Macchina, String> alimentazione;
    @FXML private TableColumn<Macchina, Integer> prezzo;
    @FXML private TableColumn<Macchina, String> tipologia;

    @FXML
    public void initialize() {


        nome.setCellValueFactory(new PropertyValueFactory<>("modello"));
        casa.setCellValueFactory(new PropertyValueFactory<>("casa"));
        km.setCellValueFactory(new PropertyValueFactory<>("km"));
        posti.setCellValueFactory(new PropertyValueFactory<>("posti"));
        proprietari.setCellValueFactory(new PropertyValueFactory<>("proprietari"));
        anno.setCellValueFactory(new PropertyValueFactory<>("anno"));
        alimentazione.setCellValueFactory(new PropertyValueFactory<>("alimentazione"));
        prezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        tipologia.setCellValueFactory(new PropertyValueFactory<>("tipologia"));

        List<Macchina> listaAuto = catalogoController.getCars();

        if (listaAuto != null) {
            ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
            tableView.setItems(data);
        }
    }

}