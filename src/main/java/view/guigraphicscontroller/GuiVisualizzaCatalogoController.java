package view.guigraphicscontroller;

import bean.CatalogoBean;
import bean.NotificaBean;
import controller.NotificheController;
import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

    private Stage stageNotifiche;

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

        if (stageNotifiche != null && stageNotifiche.isShowing()) {
            stageNotifiche.close();
            stageNotifiche = null;
            return;
        }

        NotificaBean bean = new NotificaBean();
        bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
        List<Notifica> listaNotifiche = notificheController.getStoricoNotifiche(bean);

        VBox contenitoreNotifiche = creaContenitoreBase();

        if (listaNotifiche == null || listaNotifiche.isEmpty()) {
            contenitoreNotifiche.getChildren().add(creaLabelNessunaNotifica());
        } else {
            for (Notifica n : listaNotifiche) {
                contenitoreNotifiche.getChildren().add(creaCardNotifica(n, contenitoreNotifiche));
                segnaComeLettaSeNecessario(n);
            }
        }

        configuraEMostraStage(contenitoreNotifiche, event.getScreenX(), event.getScreenY());
    }

    private VBox creaCardNotifica(Notifica n, VBox contenitorePadre) {
        VBox card = new VBox(10);
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setMinHeight(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        card.setPadding(new Insets(15));

        // Stili
        String baseStyle = "-fx-background-color: #fff7f7; -fx-border-color: #ffcccc; -fx-border-radius: 10; -fx-background-radius: 10;";
        String hoverStyle = "-fx-background-color: #ffeaea; -fx-border-color: #ffb3b3; -fx-border-radius: 10; -fx-background-radius: 10;";
        card.setStyle(baseStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        // Header (Titolo + Bottone Elimina)
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        String tipo = n.getTipo() == Notifica.Tipo.SISTEMA ? "⚙ Sistema" : "✉ Messaggio";
        Label lblTitolo = new Label(tipo);
        lblTitolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: black;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnElimina = new Button("X");
        btnElimina.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold; -fx-cursor: hand;");
        btnElimina.setOnAction(e -> eliminaNotifica(n, card, contenitorePadre));

        header.getChildren().addAll(lblTitolo, spacer, btnElimina);

        // Testo e Data
        Label lblTesto = new Label(n.getTesto());
        lblTesto.setWrapText(true);
        lblTesto.setMaxWidth(290);
        lblTesto.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        Label lblData = new Label(n.getDataCreazione().toString().replace("T", " ").substring(0, 16));
        lblData.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        card.getChildren().addAll(header, lblTesto, lblData);

        return card;
    }

    private void eliminaNotifica(Notifica n, VBox card, VBox contenitorePadre) {
        NotificaBean elimina = new NotificaBean();
        elimina.setId(String.valueOf(n.getId()));
        notificheController.eliminaNotifica(elimina);

        contenitorePadre.getChildren().remove(card);

        if (contenitorePadre.getChildren().isEmpty()) {
            contenitorePadre.getChildren().add(creaLabelNessunaNotifica());
        }
    }

    private VBox creaContenitoreBase() {
        VBox contenitore = new VBox(10);
        contenitore.setPadding(new Insets(10));
        contenitore.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-width: 1;");
        return contenitore;
    }

    private Label creaLabelNessunaNotifica() {
        Label lblVuoto = new Label("Nessuna notifica");
        lblVuoto.setStyle("-fx-font-size: 14px; -fx-text-fill: gray; -fx-padding: 20px;");
        return lblVuoto;
    }

    private void segnaComeLettaSeNecessario(Notifica n) {
        if (!n.isLetta()) {
            NotificaBean user = new NotificaBean();
            user.setId(String.valueOf(n.getId()));
            notificheController.apriNotifica(user);
        }
    }

    private void configuraEMostraStage(VBox contenitoreNotifiche, double screenX, double screenY) {
        ScrollPane scrollPane = new ScrollPane(contenitoreNotifiche);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefHeight(Math.min(400, contenitoreNotifiche.getChildren().size() * 120 + 20));
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: white;");

        stageNotifiche = new Stage();
        stageNotifiche.initStyle(StageStyle.UNDECORATED);
        stageNotifiche.setScene(new Scene(scrollPane));

        stageNotifiche.setX(screenX - 160);
        stageNotifiche.setY(screenY + 15);

        stageNotifiche.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (stageNotifiche != null && !isNowFocused) {
                stageNotifiche.close();
                stageNotifiche = null;
                controllaNotifiche();
            }
        });

        stageNotifiche.show();
    }

    private void controllaNotifiche() {
        try {
            boolean loggedIn = SessionSingleton.getInstance().isUserLoggedIn();

            if (loggedIn && lblNotifiche != null) {

                if (SessionSingleton.getInstance().getUtenteCorrente() != null) {

                    NotificaBean bean = new NotificaBean();
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