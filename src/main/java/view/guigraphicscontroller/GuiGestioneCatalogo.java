package view.guigraphicscontroller;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import controller.MainPageCatalogoController;
import exceptions.GenericSystemException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.macchina.Macchina;
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.util.List;

public class GuiGestioneCatalogo {

    private final GestioneCatalogoController gestioneCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    private final MainPageCatalogoController mainPageCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();

    @FXML private TableView<Macchina> tabellaAuto;
    @FXML private TableColumn<Macchina, String> colMarca;
    @FXML private TableColumn<Macchina, String> colModello;
    @FXML private TableColumn<Macchina, String> colAlimentazione;
    @FXML private TableColumn<Macchina, Integer> colPrezzo;
    @FXML private TableColumn<Macchina, Integer> colPosti;

    @FXML private TextField model;
    @FXML private TextField brand;
    @FXML private TextField alimentazione;
    @FXML private TextField posti;
    @FXML private TextField km;
    @FXML private TextField prezzo;
    @FXML private TextField urlFoto;

    @FXML private Button delete;
    @FXML private Button update;

    @FXML
    public void initialize() {
        configuraColonne();
        caricaDati();
        configuraClickTabella();
    }

    private void configuraColonne() {
        colMarca.setCellValueFactory(new PropertyValueFactory<>("casa"));
        colModello.setCellValueFactory(new PropertyValueFactory<>("modello"));
        colAlimentazione.setCellValueFactory(new PropertyValueFactory<>("alimentazione"));
        colPrezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        colPosti.setCellValueFactory(new PropertyValueFactory<>("posti"));
    }

    private void caricaDati() {
        try {
            List<Macchina> listaAuto = mainPageCatalogoController.getCars();
            if (listaAuto != null) {
                ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
                tabellaAuto.setItems(data);
            }
        } catch (Exception e) {
            throw new GenericSystemException("Errore nel caricamento del catalogo Admin", e);
        }
    }

    private void configuraClickTabella() {

        tabellaAuto.getSelectionModel().selectedItemProperty().addListener((osservatore, vecchiaSelezione, nuovaSelezione) -> {
            if (nuovaSelezione != null) {

                SessionSingleton.getInstance().setAutoSelezionata(nuovaSelezione);

                brand.setText(nuovaSelezione.getCasa() != null ? nuovaSelezione.getCasa() : "");
                model.setText(nuovaSelezione.getModello() != null ? nuovaSelezione.getModello() : "");
                alimentazione.setText(nuovaSelezione.getAlimentazione() != null ? nuovaSelezione.getAlimentazione() : "");
                prezzo.setText(String.valueOf(nuovaSelezione.getPrezzo()));
                km.setText(String.valueOf(nuovaSelezione.getKm()));
                posti.setText(String.valueOf(nuovaSelezione.getPosti()));
                urlFoto.setText(nuovaSelezione.getImageUrl() != null ? nuovaSelezione.getImageUrl() : "");
            }
        });
    }

    @FXML
    public void backHome(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");
    }

    @FXML
    public void delete(ActionEvent event) {
        if (SessionSingleton.getInstance().getAutoSelezionata() == null) return;

        CatalogoBean catalogoBean = new CatalogoBean();
        catalogoBean.setId(SessionSingleton.getInstance().getAutoSelezionata().getId());

        gestioneCatalogoController.removeCar(catalogoBean);

        caricaDati();
        svuotaForm();
    }

    @FXML
    public void modificaCar(ActionEvent event) {
        if (SessionSingleton.getInstance().getAutoSelezionata() == null) return;

        CatalogoBean catalogo = new CatalogoBean();
        catalogo.setId(SessionSingleton.getInstance().getAutoSelezionata().getId());

        String model1 = model.getText().trim();
        if (!model1.isEmpty()) catalogo.setModello(model1);

        String brand1 = brand.getText().trim();
        if (!brand1.isEmpty()) catalogo.setMarca(brand1);

        String alimentazione1 = alimentazione.getText().trim();
        if (!alimentazione1.isEmpty()) catalogo.setAlimentazione(alimentazione1);

        String urlFoto1 = urlFoto.getText().trim();
        if (!urlFoto1.isEmpty()) catalogo.setFoto(urlFoto1);

        try {
            String prezzo1 = prezzo.getText().trim();
            if (!prezzo1.isEmpty()) catalogo.setPrezzo(Integer.parseInt(prezzo1));

            String km1 = km.getText().trim();
            if (!km1.isEmpty()) catalogo.setKm(Integer.parseInt(km1));

            String posti1 = posti.getText().trim();
            if (!posti1.isEmpty()) catalogo.setPosti(Integer.parseInt(posti1));

            gestioneCatalogoController.modifyCar(catalogo);

            caricaDati();

        } catch (NumberFormatException e) {
            throw new GenericSystemException("Errore di compilazione: Prezzo, Km e Posti devono contenere SOLO numeri interi.");
        }
    }

    private void svuotaForm() {
        brand.clear();
        model.clear();
        alimentazione.clear();
        prezzo.clear();
        km.clear();
        posti.clear();
        urlFoto.clear();
        SessionSingleton.getInstance().setAutoSelezionata(null);
    }
}