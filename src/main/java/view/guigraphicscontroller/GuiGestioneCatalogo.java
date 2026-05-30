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
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;

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
    private TableColumn<Macchina, Double> colPrezzo;

    @FXML
    private TableColumn<Macchina, Integer> colPosti;

    private static final String BTN = "Button";

    @FXML
    public void initialize() {
        modificaSegnalata();
        configuraColonne();
        caricaDati();
        configuraClickTabella();
    }

    private void modificaSegnalata() {

        if (SessionSingleton.getInstance().getTempIdNotifica() != null) {
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
        if (auto.getId() == 0) return;

        Stage popupStage = new Stage();

        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Impostazioni: " + auto.getMarca() + " " + auto.getModello());

        TextField txtMarca = creaTextField(auto.getMarca(), "");
        TextField txtModello = creaTextField(auto.getModello(), "");
        TextField txtAnno = creaTextField(String.valueOf(auto.getAnno()), "");
        TextField txtPrezzo = creaTextField(String.valueOf(auto.getPrezzo()), "");
        TextField txtUrl = creaTextField(auto.getImageUrl(), "no_image.png");

        ComboBox<String> cbPosti = new ComboBox<>();
        cbPosti.getItems().addAll("2", "4", "5", "7", "8", "9");
        cbPosti.setValue(String.valueOf(auto.getPosti()));
        cbPosti.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Ibrida", "Elettrica", "GPL", "Metano");
        cbAlimentazione.setValue(auto.getAlimentazione());
        cbAlimentazione.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbCambio = new ComboBox<>();
        cbCambio.getItems().addAll("Manuale", "Automatica");
        cbCambio.setValue(auto.getTrasmissione());
        cbCambio.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Berlina", "Suv", "Utilitaria", "Sportiva", "Supercar", "Station Wagon");
        cbTipo.setValue(auto.getTipologia());
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        Label lblErrore = new Label("");
        lblErrore.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblErrore.setWrapText(true);

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
            lblErrore.setText("");
            try {
                CatalogoBean bean = new CatalogoBean();
                bean.setId(auto.getId());

                assegnaStringa(txtMarca.getText(), bean::setMarca);
                assegnaStringa(txtModello.getText(), bean::setModello);
                assegnaStringa(cbAlimentazione.getValue(), bean::setAlimentazione);
                assegnaStringa(cbCambio.getValue(), bean::setTrasmissione);
                assegnaStringa(cbTipo.getValue(), bean::setTipologia);
                assegnaStringa(txtUrl.getText(), bean::setFoto);

                bean.setPrezzo(txtPrezzo.getText().isEmpty() ? 0 : Double.parseDouble(txtPrezzo.getText()));
                bean.setAnno(txtAnno.getText().isEmpty() ? 0 : Integer.parseInt(txtAnno.getText()));
                bean.setPosti(cbPosti.getValue() == null ? 0 : Integer.parseInt(cbPosti.getValue()));

                gestioneCatalogoController.validaEModificaAuto(bean);

                NotificaBean modifica = new NotificaBean();

                modifica.setMacchina(auto);
                modifica.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
                modifica.setMsg("Auto modificata con successo!");

                notificheController.generaNotificaSistema(modifica);

                caricaDati();
                popupStage.close();

            } catch (NumberFormatException ex) {
                lblErrore.setText("I campi Prezzo, Anno e Posti devono contenere solo numeri.");
            } catch (IllegalArgumentException ex) {
                lblErrore.setText(ex.getMessage());
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
        layoutPopup.setAlignment(Pos.TOP_CENTER);

        Label lblTitolo = new Label("Modifica dati auto:");
        lblTitolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #204080;");

        layoutPopup.getChildren().addAll(
                lblTitolo,
                lblErrore,
                creaBoxInput("Marca:", txtMarca),
                creaBoxInput("Modello:", txtModello),
                creaBoxInput("Anno di produzione:", txtAnno),
                creaBoxInput("Prezzo (€):", txtPrezzo),
                creaBoxInput("Nome File Immagine:", txtUrl),
                creaBoxInput("Numero Posti:", cbPosti),
                creaBoxInput("Alimentazione:", cbAlimentazione),
                creaBoxInput("Trasmissione:", cbCambio),
                creaBoxInput("Tipologia:", cbTipo),
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(layoutPopup);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 380, 600);
        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setResizable(false);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private TextField creaTextField(String valore, String valoreDiDefault) {
        String testoIniziale = (valore != null && !valore.trim().isEmpty()) ? valore : valoreDiDefault;
        TextField tf = new TextField(testoIniziale);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private VBox creaBoxInput(String testoLabel, Control campoInput) {
        Label label = new Label(testoLabel);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #204080; -fx-font-size: 12px;");
        VBox box = new VBox(5, label, campoInput);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void assegnaStringa(String valore, java.util.function.Consumer<String> setter) {
        if (valore != null && !valore.trim().isEmpty()) {
            setter.accept(valore.trim());
        } else {
            setter.accept("");
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
        txtMarca.setPromptText("Es. Fiat");

        TextField txtModello = new TextField();
        txtModello.setPromptText("Es. Panda");

        TextField txtAnno = new TextField();
        txtAnno.setPromptText("Es. 2023");

        TextField txtPrezzo = new TextField();
        txtPrezzo.setPromptText("Es. 25.50");

        TextField txtUrl = new TextField();
        txtUrl.setPromptText("Es. auto.png");

        ComboBox<String> cbPosti = new ComboBox<>();
        cbPosti.setPromptText("Seleziona posti");
        cbPosti.getItems().addAll("2", "4", "5", "7", "8");

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.setPromptText("Seleziona alimentazione");
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Ibrida", "Elettrica", "GPL");

        ComboBox<String> cbCambio = new ComboBox<>();
        cbCambio.setPromptText("Seleziona trasmissione");
        cbCambio.getItems().addAll("Manuale", "Automatica");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setPromptText("Seleziona tipologia");
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


        Label lblErrore = new Label("");
        lblErrore.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblErrore.setWrapText(true);

        Button btnAdd = new Button("Aggiungi Auto in RAM");
        btnAdd.getStyleClass().add(BTN);
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        btnAdd.setOnAction(e -> {
            lblErrore.setText("");
            try {
                CatalogoBean bean = new CatalogoBean();

                assegnaStringa(txtMarca.getText(), bean::setMarca);
                assegnaStringa(txtModello.getText(), bean::setModello);
                assegnaStringa(cbAlimentazione.getValue(), bean::setAlimentazione);
                assegnaStringa(cbCambio.getValue(), bean::setTrasmissione);
                assegnaStringa(cbTipo.getValue(), bean::setTipologia);
                assegnaStringa(txtUrl.getText(), bean::setFoto);

                bean.setPrezzo(txtPrezzo.getText().isEmpty() ? 0 : Double.parseDouble(txtPrezzo.getText()));
                bean.setAnno(txtAnno.getText().isEmpty() ? 0 : Integer.parseInt(txtAnno.getText()));
                bean.setPosti(cbPosti.getValue() == null ? 0 : Integer.parseInt(cbPosti.getValue()));

                gestioneCatalogoController.validaEAggiungiAuto(bean);

                caricaDati();
                popupStage.close();

            } catch (NumberFormatException ex) {
                lblErrore.setText("Formato errato: inserisci solo numeri validi nei campi Prezzo, Anno e Posti.");

            } catch (IllegalArgumentException ex) {
                lblErrore.setText("Attenzione: " + ex.getMessage());

            } catch (Exception ex) {
                lblErrore.setText("Si è verificato un errore di sistema durante il salvataggio.");
            }
        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.TOP_CENTER);

        Label lblTitolo = new Label("Aggiungi nuova auto:");
        lblTitolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #204080;");

        layoutPopup.getChildren().addAll(
                lblTitolo,
                lblErrore,
                creaBoxInput("Marca:", txtMarca),
                creaBoxInput("Modello:", txtModello),
                creaBoxInput("Anno di produzione:", txtAnno),
                creaBoxInput("Prezzo (€):", txtPrezzo),
                creaBoxInput("Nome File Immagine:", txtUrl),
                creaBoxInput("Numero Posti:", cbPosti),
                creaBoxInput("Alimentazione:", cbAlimentazione),
                creaBoxInput("Trasmissione:", cbCambio),
                creaBoxInput("Tipologia:", cbTipo),
                btnAdd
        );

        ScrollPane scrollPane = new ScrollPane(layoutPopup);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 380, 600);
        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setResizable(false);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}