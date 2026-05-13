package view.guigraphicscontroller;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import controller.VisualizzaCatalogoController;
import exceptions.GenericSystemException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.macchina.Macchina;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class GuiGestioneCatalogo {

    private final GestioneCatalogoController gestioneCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    private final VisualizzaCatalogoController mainPageCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createMainPageCatalogoController();

    @FXML private TableView<Macchina> tabellaAuto;
    @FXML private TableColumn<Macchina, String> colMarca;
    @FXML private TableColumn<Macchina, String> colModello;
    @FXML private TableColumn<Macchina, String> colAlimentazione;
    @FXML private TableColumn<Macchina, Integer> colPrezzo;
    @FXML private TableColumn<Macchina, Integer> colPosti;

    private final Logger logger= (Logger) LogManager.getLogger(GuiGestioneCatalogo.class);

    @FXML
    public void initialize() {
        configuraColonne();
        caricaDati();
        configuraClickTabella();
    }

    private void configuraColonne() {

        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
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
        tabellaAuto.setRowFactory(tv -> {
            TableRow<Macchina> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (!row.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 2) {

                    Macchina autoSelezionata = row.getItem();
                    SessionSingleton.getInstance().setAutoSelezionata(autoSelezionata);

                    apriImpostazioniAuto(autoSelezionata);
                }
            });
            return row;
        });
    }

    private void apriImpostazioniAuto(Macchina auto) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Impostazioni: " + auto.getMarca() + " " + auto.getModello());

        TextField txtModello = new TextField(auto.getModello() != null ? auto.getModello() : "");
        TextField txtMarca = new TextField(auto.getMarca() != null ? auto.getMarca() : "");
        TextField txtPrezzo = new TextField(String.valueOf(auto.getPrezzo()));
        TextField txtAnno = new TextField(String.valueOf(auto.getAnno()));

        ComboBox<String> cbPosti = new ComboBox<>();
        cbPosti.getItems().addAll("2", "4", "5", "7", "8", "9");
        cbPosti.setValue(String.valueOf(auto.getPosti()));

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Berlina", "Suv", "Utilitaria", "Sportiva", "Supercar", "Station Wagon");
        cbTipo.setValue(auto.getTipologia());

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Ibrida", "Elettrica", "GPL", "Metano");
        cbAlimentazione.setValue(auto.getAlimentazione());

        ComboBox<String> cbCambio = new ComboBox<>();
        cbCambio.getItems().addAll("Manuale", "Automatica");
        cbCambio.setValue(auto.getTrasmissione());

        Button btnUpdate = new Button("Salva Modifiche");

        Button btnDelete = new Button("Elimina auto");

        btnUpdate.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            bean.setId(auto.getId());
            assegnaStringa(txtModello.getText(), bean::setModello);
            assegnaStringa(txtMarca.getText(), bean::setMarca);
            assegnaStringa(cbAlimentazione.getValue(), bean::setAlimentazione);
            assegnaStringa(cbCambio.getValue(), bean::setTrasmissione);
            assegnaStringa(cbTipo.getValue(), bean::setTipologia);
            if (txtPrezzo.getText() != null && !txtPrezzo.getText().trim().isEmpty()) {bean.setPrezzo(Double.parseDouble(txtPrezzo.getText().trim()));}
            assegnaIntero(txtAnno.getText(), bean::setAnno);
            assegnaIntero(cbPosti.getValue(), bean::setPosti);

            try {

                gestioneCatalogoController.modifyCar(bean);
                caricaDati();
                popupStage.close();

            } catch (NumberFormatException ex) {
                logger.error("Attenzione: Inserire valori numerici validi per Prezzo, Anno e Posti.");
            }
        });

        btnDelete.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            bean.setId(auto.getId());

            gestioneCatalogoController.removeCar(bean);

            caricaDati();

            popupStage.close();
        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        layoutPopup.getChildren().addAll(
                new Label("Modifica dati auto:"),
                txtMarca,
                txtModello,
                txtAnno,
                txtPrezzo,
                cbPosti,
                cbAlimentazione,
                cbCambio,
                cbTipo,
                btnUpdate,
                btnDelete
        );

        Scene scene = new Scene(layoutPopup, 350, 470);
        StageHandler.getSingletonInstance().loadCss(scene);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    public void btnAdd(ActionEvent event) {
        aggiungiAuto();
    }

    @FXML
    public void btnSave(ActionEvent event) throws IOException {
        gestioneCatalogoController.confermaSalvataggio();
        StageHandler.getSingletonInstance().loadPage("/view/GestioneCatalogo.fxml");
    }

    @FXML
    public void goBack(MouseEvent event) throws IOException {
        String str="/view/AdminView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    private void aggiungiAuto(){

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Aggiungi Auto");

        TextField txtModello = new TextField();
        txtModello.setPromptText("Modello");

        TextField txtMarca = new TextField();
        txtMarca.setPromptText("Marca");

        TextField txtPrezzo = new TextField();
        txtPrezzo.setPromptText("Prezzo");

        ComboBox<String> cbPosti = new ComboBox<>();
        cbPosti.setPromptText("Posti");
        cbPosti.getItems().addAll("2","4","5","7","8");

        TextField txtAnno = new TextField();
        txtAnno.setPromptText("Anno");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setPromptText("Tipologia");
        cbTipo.getItems().addAll("Berlina", "Suv", "Utilitaria", "Sportiva", "Supercar");

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.setPromptText("Alimentazione");
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Ibrida", "Elettrica", "GPL");

        ComboBox<String> cbCambio = new ComboBox<>();
        cbCambio.setPromptText("Trasmissione");
        cbCambio.getItems().addAll("Manuale", "Automatica");

        Button btnAdd = new Button("Aggiungi");
        btnAdd.getStyleClass().add("Button");

        btnAdd.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            assegnaStringa(txtModello.getText(), bean::setModello);
            assegnaStringa(txtMarca.getText(), bean::setMarca);
            assegnaStringa(cbAlimentazione.getValue(), bean::setAlimentazione);
            assegnaStringa(cbCambio.getValue(), bean::setTrasmissione);
            assegnaStringa(cbTipo.getValue(), bean::setTipologia);

            try {
                assegnaIntero(txtPrezzo.getText(), bean::setPrezzo);
                assegnaIntero(txtAnno.getText(), bean::setAnno);
                assegnaIntero(cbPosti.getValue(), bean::setPosti);
            } catch (NumberFormatException ex) {
                logger.error("Attenzione: Inserire valori numerici validi per Prezzo, Anno e Posti.");
                return;
            }

            gestioneCatalogoController.salvaAutoRam(bean);
            caricaDati();
            popupStage.close();

        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);


        layoutPopup.getChildren().addAll(
                new Label("Aggiungi nuova auto:"),
                txtModello,
                txtMarca,
                txtAnno,
                txtPrezzo,
                cbPosti,
                cbAlimentazione,
                cbTipo,
                cbCambio,
                btnAdd
        );

        Scene scene = new Scene(layoutPopup, 300, 450);

        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void assegnaStringa(String valore, Consumer<String> setter) {
        if (valore != null && !valore.trim().isEmpty()) {
            setter.accept(valore.trim());
        }
    }

    private void assegnaIntero(String valore, IntConsumer setter) throws NumberFormatException {
        if (valore != null && !valore.trim().isEmpty()) {
            setter.accept(Integer.parseInt(valore.trim()));
        }
    }
}