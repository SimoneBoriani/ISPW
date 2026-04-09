package view.guiwindowmanager;

import bean.CatalogoBean;
import controller.CatalogoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

import java.io.IOException;
import java.util.List;

public class GuiCatalogoController {

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
    private TextField txtBrand;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtKm;
    @FXML
    private TextField txtPosti;
    @FXML
    private TextField txtAlimentation;
    @FXML
    private ImageView searchIcon;

    @FXML
    public void researchCars(MouseEvent event) throws IOException {

    }

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

        CatalogoController catalogoController = new CatalogoController();

        List<Macchina> listaAuto = catalogoController.getCars();

        if (listaAuto != null) {
            ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
            tableView.setItems(data);
        }
    }

    @FXML
    public void refreshImage(MouseEvent e){
        ObservableList<Macchina> data = FXCollections.observableArrayList(DaoFactory.getDaoSingletonFactory().createMacchinaDao().getCars());
        tableView.setItems(data);
        tableView.refresh();
    }

    @FXML
    void menuRicerca(MouseEvent event) {

        ContextMenu popup = new ContextMenu();

        TextField txtmodel = new TextField();
        txtmodel.setPromptText("Modello");

        TextField txtbrand = new TextField();
        txtbrand.setPromptText("Marca");

        TextField txtprice = new TextField();
        txtprice.setPromptText("Prezzo Max");

        TextField txtkm = new TextField();
        txtkm.setPromptText("Km Max");

        TextField txtalimentation = new TextField();
        txtalimentation.setPromptText("Alimentazione");

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

            // 1. Estraiamo i dati puri dalle caselle di testo
            String model = txtmodel.getText().trim();
            String brand = txtbrand.getText().trim();
            String alimentation = txtalimentation.getText().trim();

            int price = 0;
            try {
                if (!txtprice.getText().trim().isEmpty()) {
                    price = Integer.parseInt(txtprice.getText().trim());
                }
            } catch (NumberFormatException ex) {
                System.out.println("Formato prezzo non valido, ignoro il filtro prezzo.");
            }

            int carKm = 0;
            try {
                if (!txtkm.getText().trim().isEmpty()) {
                    carKm = Integer.parseInt(txtkm.getText().trim());
                }
            } catch (NumberFormatException ex) {
                System.out.println("Formato km non valido, ignoro il filtro km.");
            }

            // 2. Impacchettiamo i dati nel DTO (CatalogoBean)
            CatalogoBean bean = new CatalogoBean();
            bean.setModello(model);
            bean.setMarca(brand);
            bean.setAlimentazione(alimentation);
            bean.setPrezzo(price);
            bean.setKm(carKm);

            // 3. Deleghiamo la ricerca DIRETTAMENTE AL CONTROLLER APPLICATIVO!
            try {
                CatalogoController appController = new CatalogoController();
                List<Macchina> risultati = appController.research(bean);

                // Se la ricerca va a buon fine, popoliamo la tabella
                ObservableList<Macchina> data = FXCollections.observableArrayList(risultati);
                tableView.setItems(data);
                tableView.refresh();

                // Chiudiamo il menu a tendina
                popup.hide();

            } catch (exceptions.CarNotFoundException ex) {
                // Se viene lanciata l'eccezione (nessuna auto trovata), svuotiamo la tabella
                tableView.getItems().clear();
                tableView.refresh();
                System.out.println("Ricerca vuota: " + ex.getMessage());
                // Opzionale: errorCatalogText.setText("Nessun risultato trovato");

            } catch (Exception ex) {
                System.out.println("Errore generico di sistema: " + ex.getMessage());
            }
        });

        popup.getItems().addAll(itemModel, itemBrand, itemKm, itemAlimentation, itemPrice, new SeparatorMenuItem(), btnCerca);

        popup.show(searchIcon, event.getScreenX(), event.getScreenY());
    }
}