package view.guigraphicscontroller;

import bean.CatalogoBean;
import bean.SegnalazioneBean;
import controller.NotificheController;
import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.notifiche.Notifica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SessionSingleton;
import controller.VisualizzaCatalogoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.macchina.Macchina;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


public class GuiVisualizzaCatalogoController {

    private final VisualizzaCatalogoController visualizzaCatalogoController= ControllerFactory.getGraphicalSingletonFactory().createVisualizzaCatalogoController();
    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    @FXML
    private Button btnAccedi;

    @FXML
    private Button btnLogout;

    @FXML
    private Label lblNotifiche;

    @FXML
    private ListView<Macchina> carListView;

    @FXML
    private ContextMenu menuNotifiche;

    private static final Logger logger = LogManager.getLogger(GuiVisualizzaCatalogoController.class.getName());

    @FXML
    public void initialize() {

        configuraCatalogo();
        configuraBottoni();
        controllaNotifiche();

    }

    private void apriDettaglio(Macchina auto) {
        try {

            SessionSingleton.getInstance().setAutoSelezionata(auto);
            StageHandler.getSingletonInstance().loadPage("/view/NoleggioView.fxml");

        } catch (IOException e) {
            throw new GenericSystemException("Errore nel caricamento della pagina DettaglioAuto: ",e);
        }
    }

    private void configuraCatalogo() {
        try {
            if (carListView != null) {
                List<Macchina> listaAuto = visualizzaCatalogoController.getCars();

                if (listaAuto != null && !listaAuto.isEmpty()) {
                    ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
                    carListView.setItems(data);
                    carListView.setCellFactory(param -> new CarCellAuto());
                }

                carListView.setOnMouseClicked(event -> {
                    Macchina selezionata = carListView.getSelectionModel().getSelectedItem();
                    if (selezionata != null) {
                        apriDettaglio(selezionata);
                    }
                });
            }
        } catch (Exception e) {
            throw new GenericSystemException("Errore Caricamento", e);
        }
    }

    private void configuraBottoni() {

        boolean loggedIn = SessionSingleton.getInstance().isUserLoggedIn();

        if (loggedIn) {
            btnAccedi.setVisible(false);
            btnAccedi.setManaged(false);
            lblNotifiche.setVisible(true);
            lblNotifiche.setManaged(true);
            if (btnLogout != null) {
                btnLogout.setVisible(true);
                btnLogout.setManaged(true);
            }
        } else {
            btnAccedi.setVisible(true);
            btnAccedi.setManaged(true);
            lblNotifiche.setVisible(false);
            lblNotifiche.setManaged(false);
            if (btnLogout != null) {
                btnLogout.setVisible(false);
                btnLogout.setManaged(false);
            }
        }
    }

    @FXML
    public void btnAccediOnAction(ActionEvent event) throws IOException {

        String str= "/view/Login.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void btnLogoutOnAction(ActionEvent event) throws IOException {

        SessionSingleton.getInstance().logout();
        StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");

    }

    @FXML
    public void goToProfile(MouseEvent event) throws IOException{
        String str="/view/Profilo.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

    @FXML
    public void goToGarage(MouseEvent event) throws IOException{
        String str="/view/Garage.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void refresh(MouseEvent event){
        configuraCatalogo();
    }

    @FXML
    public void research(MouseEvent event){
        apriPopupFiltro();
    }

    private void assegnaSeValido(String valore, Consumer<String> setter) {
        if (valore != null && !valore.trim().isEmpty()) {
            setter.accept(valore.trim());
        }
    }

    private void apriPopupFiltro() {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Ricerca auto");

        TextField txtMarca = new TextField();
        txtMarca.setPromptText("Marca");

        TextField txtModello = new TextField();
        txtModello.setPromptText("Modello");

        TextField txtPrezzo = new TextField();
        txtPrezzo.setPromptText("Prezzo");

        ComboBox<String> cbAlimentazione = new ComboBox<>();
        cbAlimentazione.setPromptText("Alimentazione");
        cbAlimentazione.getItems().addAll("Benzina", "Diesel", "Elettrica", "GPL", "Ibrida");

        ComboBox<String> cbTrasmissione = new ComboBox<>();
        cbTrasmissione.setPromptText("Trasmissione");
        cbTrasmissione.getItems().addAll("Manuale", "Automatica");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.setPromptText("Tipologia");
        cbTipo.getItems().addAll("Berlina", "Suv", "Utilitaria", "Sportiva", "Supercar");

        Button btnAvviaRicerca = new Button("Avvia Ricerca");

        btnAvviaRicerca.getStyleClass().add("Button");

        btnAvviaRicerca.setOnAction(e -> {

            CatalogoBean bean = new CatalogoBean();

            assegnaSeValido(txtMarca.getText(), bean::setMarca);
            assegnaSeValido(txtModello.getText(), bean::setModello);
            assegnaSeValido(cbAlimentazione.getValue(), bean::setAlimentazione);
            assegnaSeValido(cbTrasmissione.getValue(), bean::setTrasmissione);
            assegnaSeValido(cbTipo.getValue(), bean::setTipologia);

            String prezzoStr = txtPrezzo.getText();
            if (prezzoStr != null && !prezzoStr.trim().isEmpty()) {
                try {
                    bean.setPrezzo(Integer.parseInt(prezzoStr.trim()));
                } catch (NumberFormatException ex) {
                    logger.error("Errore: Inserisci solo numeri nel Prezzo.");
                    return;
                }
            }

            try {

                List<Macchina> autoTrovate = visualizzaCatalogoController.research(bean);

                if (carListView != null && autoTrovate != null) {
                        ObservableList<Macchina> data = FXCollections.observableArrayList(autoTrovate);
                        carListView.setItems(data);
                        carListView.setCellFactory(param -> new CarCellAuto());
                }
            } catch (CarNotFoundException ex) {
                carListView.getItems().clear();
            }

            popupStage.close();

        });

        VBox layoutPopup = new VBox(15);
        layoutPopup.setPadding(new Insets(20));
        layoutPopup.setAlignment(Pos.CENTER);

        layoutPopup.getChildren().addAll(
                new Label("Filtri di ricerca:"),
                txtMarca,
                txtModello,
                txtPrezzo,
                cbAlimentazione,
                cbTrasmissione,
                cbTipo,
                btnAvviaRicerca
        );

        Scene scene = new Scene(layoutPopup, 300, 350);

        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setScene(scene);
        popupStage.showAndWait();

    }

    @FXML
    public void apriPopupNotifiche(MouseEvent event) {

        if (!SessionSingleton.getInstance().isUserLoggedIn()) {
            return;
        }

        if (menuNotifiche != null && menuNotifiche.isShowing()) {
            menuNotifiche.hide();
            return;
        }

        SegnalazioneBean bean = new SegnalazioneBean();

        bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());

        List<Notifica> listaNotifiche = notificheController.getStoricoNotifiche(bean);

        menuNotifiche = new ContextMenu();

        if (listaNotifiche == null || listaNotifiche.isEmpty()) {

            MenuItem vuoto = new MenuItem("Nessuna notifica");

            vuoto.setDisable(true);

            menuNotifiche.getItems().add(vuoto);

        } else {

            for (Notifica n : listaNotifiche) {

                VBox card = new VBox(12);

                card.setPrefWidth(320);
                card.setMinWidth(320);
                card.setMaxWidth(320);

                card.setPrefHeight(110);

                card.setPadding(new Insets(15));

                String tipo =
                        n.getTipo() == Notifica.Tipo.SISTEMA
                                ? "⚙ Sistema"
                                : "✉ Messaggio";

                Label lblTitolo = new Label(tipo);

                lblTitolo.setStyle(
                        "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;" +
                                "-fx-text-fill: black;"
                );

                Label lblTesto = new Label(n.getTesto());

                lblTesto.setWrapText(true);

                lblTesto.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-text-fill: black;"
                );

              Label lblData = new Label(
                        n.getDataCreazione()
                                .toString()
                                .replace("T", " ")
                                .substring(0,16)
                );

                lblData.setStyle(
                        "-fx-font-size: 11px;" +
                                "-fx-text-fill: #666666;"
                );

                card.getChildren().addAll(
                        lblTitolo,
                        lblTesto,
                        lblData
                );

                String baseStyle =
                        "-fx-background-color: #fff7f7;" +
                                "-fx-border-color: #ffcccc;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;";

                  String hoverStyle =
                        "-fx-background-color: #ffeaea;" +
                                "-fx-border-color: #ffb3b3;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;";

                card.setStyle(baseStyle);

                card.setOnMouseEntered(e -> card.setStyle(hoverStyle));

                card.setOnMouseExited(e -> card.setStyle(baseStyle));

                if (!n.isLetta()){
                    SegnalazioneBean user = new SegnalazioneBean();
                    user.setId(n.getId());
                    notificheController.apriNotifica(user);
                }

                CustomMenuItem item = new CustomMenuItem(card);
                item.setHideOnClick(false);

                menuNotifiche.getItems().add(item);
            }
        }

        menuNotifiche.show(
                ((javafx.scene.Node) event.getSource()),
                event.getScreenX(),
                event.getScreenY()
        );
        controllaNotifiche();
    }

    private void controllaNotifiche() {
        try {
            boolean loggedIn = SessionSingleton.getInstance().isUserLoggedIn();

            if (loggedIn && lblNotifiche != null) {

                if (SessionSingleton.getInstance().getUtenteCorrente() != null) {

                    SegnalazioneBean bean = new SegnalazioneBean();
                    bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
                    List<Notifica> daLeggere = notificheController.getNotificheDaLeggere(bean);

                    if (daLeggere != null && !daLeggere.isEmpty()) {
                        lblNotifiche.setText(String.valueOf(daLeggere.size()));
                        lblNotifiche.setVisible(true);
                        lblNotifiche.setManaged(true);
                    } else {
                        lblNotifiche.setVisible(false);
                        lblNotifiche.setManaged(false);
                    }
                }
            } else if (lblNotifiche != null) {
                lblNotifiche.setVisible(false);
                lblNotifiche.setManaged(false);
            }
        } catch (Exception e) {

            logger.error("Errore nel caricamento delle notifiche: {} ",e.getMessage());
            if (lblNotifiche != null) {
                lblNotifiche.setVisible(false);
                lblNotifiche.setManaged(false);
            }
        }
    }
}