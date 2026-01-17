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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


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
    Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    void menuRicerca(MouseEvent event) {

        ContextMenu popup = new ContextMenu();

        TextField txtmodel = new TextField();
        txtmodel.setPromptText("Modello");

        TextField txtbrand = new TextField();
        txtbrand.setPromptText("Marca");

        TextField txtprice = new TextField();
        txtprice.setPromptText("Prezzo Max"); // Meglio specificare che è un tetto massimo

        TextField txtkm = new TextField();
        txtkm.setPromptText("Km Max");

        TextField txtalimentation = new TextField();
        txtalimentation.setPromptText("Alimentazione");

        // Impediamo che il menu si chiuda quando clicchi sulle caselle di testo
        CustomMenuItem itemModel = new CustomMenuItem(txtmodel);
        itemModel.setHideOnClick(false);

        CustomMenuItem itemBrand = new CustomMenuItem(txtbrand);
        itemBrand.setHideOnClick(false);

        CustomMenuItem itemPrice = new CustomMenuItem(txtprice);
        itemPrice.setHideOnClick(false);

        CustomMenuItem itemKm = new CustomMenuItem(txtkm);
        itemKm.setHideOnClick(false);

        CustomMenuItem itemAlimentation = new CustomMenuItem(txtalimentation);
        itemAlimentation.setHideOnClick(false);

        MenuItem btnCerca = new MenuItem("Ricerca");
        btnCerca.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            String model = txtmodel.getText().trim();
            String brand = txtbrand.getText().trim();
            String alimentation = txtalimentation.getText().trim();

            int price = 0;
            try {
                if (!txtprice.getText().trim().isEmpty()) {
                    price = Integer.parseInt(txtprice.getText().trim());
                }
            } catch (NumberFormatException ex) {
                logger.info("Formato prezzo non valido, ignoro il filtro prezzo.");
            }

            int carKm = 0;
            try {
                if (!txtkm.getText().trim().isEmpty()) {
                    carKm = Integer.parseInt(txtkm.getText().trim());
                }
            } catch (NumberFormatException ex) {
                logger.info("Formato km non valido, ignoro il filtro km.");
            }

            bean.setModello(model.isEmpty() ? null : model);
            bean.setMarca(brand.isEmpty() ? null : brand);
            bean.setAlimentazione(alimentation.isEmpty() ? null : alimentation);
            bean.setPrezzo(price);
            bean.setKm(carKm);

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
            logger.info(e.getMessage());
        }
        return researchedCars;

    }
}
