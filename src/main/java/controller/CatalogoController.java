package controller;

import bean.CatalogoBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.macchina.Macchina;
import model.macchina.dao.DaoMacchine;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CatalogoController {

    @FXML
    private TableView<Macchina> tableView;
    @FXML
    private TableColumn<CatalogoBean, String> nome;
    @FXML
    private TableColumn<CatalogoBean, String> casa;
    @FXML
    private TableColumn<CatalogoBean, Integer> km;
    @FXML
    private TableColumn<CatalogoBean, Integer> posti;
    @FXML
    private TableColumn<CatalogoBean, Integer> proprietari;
    @FXML
    private TableColumn<CatalogoBean, Integer> anno;
    @FXML
    private TableColumn<CatalogoBean, String> alimentazione;
    @FXML
    private TableColumn<CatalogoBean, Integer> prezzo;
    @FXML
    private TableColumn<CatalogoBean, String> tipologia;
    @FXML
    private ImageView searchIcon;

    @FXML
    void menuRicerca(MouseEvent event) {

        ContextMenu popup = new ContextMenu();

        TextField txtmodel = new TextField();
        txtmodel.setPromptText("Modello");


        TextField txtbrand = new TextField();
        txtbrand.setPromptText("Marca");


        TextField txtprice = new TextField();
        txtprice.setPromptText("Prezzo");


        TextField txtkm = new TextField();
        txtkm.setPromptText("Km");


        TextField txtalimentation = new TextField();
        txtalimentation.setPromptText("Alimentazione");


        CustomMenuItem itemModel = new CustomMenuItem(txtmodel);
        CustomMenuItem itemBrand = new CustomMenuItem(txtbrand);
        CustomMenuItem itemPrice = new CustomMenuItem(txtprice);
        CustomMenuItem itemKm = new CustomMenuItem(txtkm);
        CustomMenuItem itemAlimentation = new CustomMenuItem(txtalimentation);


        itemModel.setHideOnClick(false);
        itemBrand.setHideOnClick(false);
        itemPrice.setHideOnClick(false);
        itemKm.setHideOnClick(false);
        itemAlimentation.setHideOnClick(false);

        MenuItem btnCerca = new MenuItem("Ricerca");
        btnCerca.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            String model = txtmodel.getText();
            int price=Integer.parseInt(txtprice.getText());
            String brand = txtbrand.getText();
            int km = Integer.parseInt(txtkm.getText());
            String alimentation=txtalimentation.getText();


            bean.setAlimentazione(alimentation);
            bean.setKm(km);
            bean.setPrezzo(price);
            bean.setMarca(brand);
            bean.setModello(model);

            bean.sendInfo();

            researchRefresh(researchedCars(bean));
        });

        popup.getItems().addAll(itemModel, itemBrand, itemKm, itemAlimentation, itemPrice, new SeparatorMenuItem(), btnCerca);

        popup.show(searchIcon, event.getScreenX(), event.getScreenY());
    }



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
        tipologia.setCellValueFactory(new PropertyValueFactory<>("Tipologia"));

        ObservableList<Macchina> data = FXCollections.observableArrayList(CatalogoBean.getCars());
        tableView.setItems(data);
    }

    @FXML
    public void refreshTable(MouseEvent event){

        ObservableList<Macchina> data = FXCollections.observableArrayList(DaoMacchine.getCars());
        tableView.setItems(data);
        tableView.refresh();
    }
    public void researchRefresh(List<Macchina> lista){
        ObservableList<Macchina> data = FXCollections.observableArrayList(lista);
        tableView.setItems(data);
        tableView.refresh();
    }

    public List<Macchina> researchedCars(CatalogoBean catalogoBean){

        List<Macchina> researchedCars = new ArrayList<>();
        try {
           researchedCars=DaoMacchine.research(catalogoBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return researchedCars;

    }
}
