package controller;

import bean.MacchineBean;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Macchina;

import java.awt.event.ActionEvent;


public class MacchineController {

    @FXML
    private TableView<Macchina> tableView;
    @FXML
    private TableColumn<MacchineBean, String> nome;
    @FXML
    private TableColumn<MacchineBean, String> casa;
    @FXML
    private TableColumn<MacchineBean, Integer> km;
    @FXML
    private TableColumn<MacchineBean, Integer> posti;
    @FXML
    private TableColumn<MacchineBean, Integer> proprietari;
    @FXML
    private TableColumn<MacchineBean, Integer> anno;
    @FXML
    private TableColumn<MacchineBean, String> alimentazione;
    @FXML
    private TableColumn<MacchineBean, Integer> prezzo;
    @FXML
    private TableColumn<MacchineBean, Integer> sconto;

    @FXML
    public void initialize() {

        nome.setCellValueFactory(new PropertyValueFactory<>("Modello"));
        casa.setCellValueFactory(new PropertyValueFactory<>("Casa"));
        km.setCellValueFactory(new PropertyValueFactory<>("Km"));
        posti.setCellValueFactory(new PropertyValueFactory<>("Posti"));
        proprietari.setCellValueFactory(new PropertyValueFactory<>("Proprietari"));
        anno.setCellValueFactory(new PropertyValueFactory<>("Anno"));
        alimentazione.setCellValueFactory(new PropertyValueFactory<>("Alimentazione"));
        prezzo.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        sconto.setCellValueFactory(new PropertyValueFactory<>("Sconto"));

        loadata();
    }

    private void loadata() {
        tableView.setItems(FXCollections.observableArrayList(MacchineBean.getCars()));
    }
}
