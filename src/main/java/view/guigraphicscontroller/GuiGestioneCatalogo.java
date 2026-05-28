package view.guigraphicscontroller;

import bean.CatalogoBean;
import bean.NotificaBean;
import controller.GestioneCatalogoController;
import controller.NotificheController;
import exceptions.GenericSystemException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    @FXML
    private TableView<Macchina> tabellaAuto;

    @FXML
    private TableColumn<Macchina, String> colMarca;

    @FXML
    private TableColumn<Macchina, String> colModello;

    @FXML
    private TableColumn<Macchina, String> colAlimentazione;

    @FXML
    private TableColumn<Macchina, Integer> colPrezzo;

    @FXML
    private TableColumn<Macchina, Integer> colPosti;

    private static final String BTN="Button";
    private final Logger logger = (Logger) LogManager.getLogger(GuiGestioneCatalogo.class);

    @FXML
    public void initialize() {

        modificaSegnalata();
        configuraColonne();
        caricaDati();
        configuraClickTabella();
    }

    private void modificaSegnalata(){

        if(SessionSingleton.getInstance().getTempIdNotifica() != null) {

            Macchina modifica = gestioneCatalogoController.createAutoSegnalata();
            Platform.runLater(() -> apriImpostazioniAuto(modifica));
            SessionSingleton.getInstance().setTempIdNotifica(null);
            SessionSingleton.getInstance().setTempMarca(null);
            SessionSingleton.getInstance().setTempModello(null);
        }
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
            List<Macchina> listaAuto = gestioneCatalogoController.getCars();
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

        if (auto.getId() != 0) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Impostazioni: " + auto.getMarca() + " " + auto.getModello());

        TextField txtMarca = new TextField(auto.getMarca() != null ? auto.getMarca() : "");
        TextField txtModello = new TextField(auto.getModello() != null ? auto.getModello() : "");
        TextField txtAnno = new TextField(String.valueOf(auto.getAnno()));
        TextField txtPrezzo = new TextField(String.valueOf(auto.getPrezzo()));
        TextField txtUrl = new TextField(auto.getImageUrl() != null ? auto.getImageUrl() : "no_image.png");

        ComboBox<String> cbPosti = new ComboBox<>();
        cbPosti.getItems().addAll("2", "4", "5", "7", "8", "9");
        cbPosti.setValue(String.valueOf(auto.getPosti()));

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Ibrida", "Elettrica", "GPL", "Metano");
        cbAlimentazione.setValue(auto.getAlimentazione());

        ComboBox<String> cbCambio = new ComboBox<>();
        cbCambio.getItems().addAll("Manuale", "Automatica");
        cbCambio.setValue(auto.getTrasmissione());

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Berlina", "Suv", "Utilitaria", "Sportiva", "Supercar", "Station Wagon");
        cbTipo.setValue(auto.getTipologia());

        txtMarca.setMaxWidth(Double.MAX_VALUE);
        txtModello.setMaxWidth(Double.MAX_VALUE);
        txtAnno.setMaxWidth(Double.MAX_VALUE);
        txtPrezzo.setMaxWidth(Double.MAX_VALUE);
        txtUrl.setMaxWidth(Double.MAX_VALUE);
        cbPosti.setMaxWidth(Double.MAX_VALUE);
        cbAlimentazione.setMaxWidth(Double.MAX_VALUE);
        cbCambio.setMaxWidth(Double.MAX_VALUE);
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        Button btnUpdate = new Button("Salva Modifiche");
        btnUpdate.getStyleClass().add(BTN);

        Button btnDelete = new Button("Elimina auto");
        btnDelete.getStyleClass().add(BTN);

        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnUpdate, Priority.ALWAYS);
        HBox.setHgrow(btnDelete, Priority.ALWAYS);
        HBox buttonBox = new HBox(10, btnUpdate, btnDelete);
        buttonBox.setAlignment(Pos.CENTER);

        btnUpdate.setOnAction(e -> {
            CatalogoBean bean = new CatalogoBean();
            bean.setId(auto.getId());

            assegnaStringa(txtModello.getText(), bean::setModello);
            assegnaStringa(txtMarca.getText(), bean::setMarca);
            assegnaStringa(cbAlimentazione.getValue(), bean::setAlimentazione);
            assegnaStringa(cbCambio.getValue(), bean::setTrasmissione);
            assegnaStringa(cbTipo.getValue(), bean::setTipologia);
            assegnaStringa(txtUrl.getText(), bean::setFoto);

            try {

                if (txtPrezzo.getText() != null || txtPrezzo.getText().isEmpty()) {
                    bean.setPrezzo(Double.parseDouble(txtPrezzo.getText()));
                }

                assegnaIntero(txtAnno.getText(), bean::setAnno);
                assegnaIntero(cbPosti.getValue(), bean::setPosti);

                gestioneCatalogoController.modifyCar(bean);

                NotificaBean modifica = new NotificaBean();

                modifica.setMacchina(auto);
                modifica.setMsg("Auto modifica con successo!");


                notificheController.generaNotificaSistema(modifica);
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
                txtUrl,
                cbPosti,
                cbAlimentazione,
                cbCambio,
                cbTipo,
                buttonBox
        );

        Scene scene = new Scene(layoutPopup, 350, 520);
        StageHandler.getSingletonInstance().loadCss(scene);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
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
        String str = "/view/AdminView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    private void aggiungiAuto() {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Aggiungi Auto");

        TextField txtMarca = new TextField();
        txtMarca.setPromptText("Marca");

        TextField txtModello = new TextField();
        txtModello.setPromptText("Modello");

        TextField txtAnno = new TextField();
        txtAnno.setPromptText("Anno");

        TextField txtPrezzo = new TextField();
        txtPrezzo.setPromptText("Prezzo");

        TextField txtUrl = new TextField();
        txtUrl.setPromptText("URL FOTO");

        ComboBox<String> cbPosti = new ComboBox<>();
        cbPosti.setPromptText("Posti");
        cbPosti.getItems().addAll("2", "4", "5", "7", "8");

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.setPromptText("Alimentazione");
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Ibrida", "Elettrica", "GPL");

        ComboBox<String> cbCambio = new ComboBox<>();
        cbCambio.setPromptText("Trasmissione");
        cbCambio.getItems().addAll("Manuale", "Automatica");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setPromptText("Tipologia");
        cbTipo.getItems().addAll("Berlina", "Suv", "Utilitaria", "Sportiva", "Supercar");


        txtMarca.setMaxWidth(Double.MAX_VALUE);
        txtModello.setMaxWidth(Double.MAX_VALUE);
        txtAnno.setMaxWidth(Double.MAX_VALUE);
        txtPrezzo.setMaxWidth(Double.MAX_VALUE);
        txtUrl.setMaxWidth(Double.MAX_VALUE);
        cbPosti.setMaxWidth(Double.MAX_VALUE);
        cbAlimentazione.setMaxWidth(Double.MAX_VALUE);
        cbCambio.setMaxWidth(Double.MAX_VALUE);
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        Button btnAdd = new Button("Aggiungi");
        btnAdd.getStyleClass().add(BTN);
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> {
            CatalogoBean bean = new CatalogoBean();

            assegnaStringa(txtModello.getText(), bean::setModello);
            assegnaStringa(txtMarca.getText(), bean::setMarca);
            assegnaStringa(cbAlimentazione.getValue(), bean::setAlimentazione);
            assegnaStringa(cbCambio.getValue(), bean::setTrasmissione);
            assegnaStringa(cbTipo.getValue(), bean::setTipologia);
            assegnaStringa(txtUrl.getText(), bean::setFoto);

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
                txtMarca,
                txtModello,
                txtAnno,
                txtPrezzo,
                txtUrl,
                cbPosti,
                cbAlimentazione,
                cbCambio,
                cbTipo,
                btnAdd
        );

        Scene scene = new Scene(layoutPopup, 300, 500);
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