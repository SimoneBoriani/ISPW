package view.guigraphicscontroller;

import bean.NotificaBean;
import controller.NotificheController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.notifiche.Notifica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SessionSingleton;
import utils.StageHandler;
import javafx.scene.input.MouseEvent;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GuiAdminViewController {

    @FXML
    private Label lblNotifiche;

    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();
    private static final Logger log = LogManager.getLogger(GuiAdminViewController.class);

    @FXML
    public void goToManage(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/GestioneCatalogo.fxml");
    }

    @FXML
    public void goToStorico(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/StoricoNoleggi.fxml");
    }

    @FXML
    public void goToProfit(ActionEvent event) throws IOException {
        StageHandler.getSingletonInstance().loadPage("/view/StoricoProfitti.fxml");
    }

    @FXML
    public void logOut(MouseEvent event) throws IOException {
        SessionSingleton.getInstance().logout();
        StageHandler.getSingletonInstance().loadPage("/view/CatalogoView.fxml");
    }

    @FXML
    public void apriPopupNotifiche(MouseEvent event) {
        if (!SessionSingleton.getInstance().isUserLoggedIn()) {
            return;
        }
        List<Notifica> listaNotifiche = recuperaStoricoNotifiche();

        VBox contenitore = new VBox(10);
        contenitore.setPadding(new Insets(15));

        popolaContenitore(contenitore, listaNotifiche);

        mostraStageNotifiche(contenitore);
        controllaNotifiche();
    }

    private List<Notifica> recuperaStoricoNotifiche() {
        NotificaBean bean = new NotificaBean();
        bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
        return notificheController.getStoricoNotifiche(bean);
    }

    private void popolaContenitore(VBox contenitore, List<Notifica> listaNotifiche) {
        if (listaNotifiche == null || listaNotifiche.isEmpty()) {
            contenitore.getChildren().add(new Label("Nessuna notifica"));
            return;
        }

        for (Notifica n : listaNotifiche) {
            HBox card = creaCardNotifica(n, contenitore);
            contenitore.getChildren().add(card);
        }
    }

    private HBox creaCardNotifica(Notifica n, VBox contenitore) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);

        applicaStileEConguaglioLettura(card, n);

        VBox testi = creaSezioneTesto(n);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox azioni = creaSezioneAzioni(n, card, contenitore);

        card.getChildren().addAll(testi, spacer, azioni);
        return card;
    }

    private void applicaStileEConguaglioLettura(HBox card, Notifica n) {
        if (!n.isLetta()) {
            card.setStyle("-fx-background-color: #fff7f7; -fx-border-color: #ffcccc; -fx-border-radius: 8; -fx-background-radius: 8;");
            NotificaBean admin = new NotificaBean();
            admin.setId(String.valueOf(n.getId()));
            notificheController.apriNotifica(admin);
        } else {
            card.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 8; -fx-background-radius: 8;");
        }
    }

    private VBox creaSezioneTesto(Notifica n) {
        VBox testi = new VBox(5);

        Label titolo = new Label(n.getTipo().toString());
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label auto = new Label();

        if(n.getAuto() != null){
        auto.setText("Auto: " + n.getAuto());
        auto.setWrapText(true);
        }

        Label testo = new Label("Testo: " + n.getTesto());
        testo.setWrapText(true);

        LocalDateTime dataCreazione = n.getDataCreazione();
        String stringaData = dataCreazione != null ? dataCreazione.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
        Label data = new Label(stringaData);
        data.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        testi.getChildren().addAll(titolo, auto, testo, data);
        return testi;
    }

    private VBox creaSezioneAzioni(Notifica n, HBox card, VBox contenitore) {
        VBox azioni = new VBox(10);
        azioni.setAlignment(Pos.CENTER_RIGHT);

        Button btnElimina = new Button("🗑 Elimina");
        btnElimina.setOnAction(e -> {
            NotificaBean elimina = new NotificaBean();
            elimina.setId(String.valueOf(n.getId()));
            notificheController.eliminaNotifica(elimina);
            contenitore.getChildren().remove(card);
            controllaNotifiche();
        });

        if ("MESSAGGIO".equalsIgnoreCase(n.getTipo().toString())) {
            Button btnImpostazioni = new Button("⚙ Impostazioni");
            btnImpostazioni.setOnAction(e -> apriImpostazioniNotifica(e, n));
            azioni.getChildren().addAll(btnImpostazioni, btnElimina);
        } else {
            azioni.getChildren().add(btnElimina);
        }

        return azioni;
    }

    private void mostraStageNotifiche(VBox contenitore) {
        ScrollPane scrollPane = new ScrollPane(contenitore);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 420, 500);

        Stage stage = new Stage();
        stage.setTitle("Notifiche");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void controllaNotifiche() {
        try {
            if (SessionSingleton.getInstance().isUserLoggedIn() && lblNotifiche != null && SessionSingleton.getInstance().getUtenteCorrente() != null) {

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

        } catch (Exception e) {
            if (lblNotifiche != null) {
                lblNotifiche.setVisible(false);
                lblNotifiche.setManaged(false);
            }
        }
    }


    private void apriImpostazioniNotifica(ActionEvent event, Notifica n) {
        try {

            String autoCompleta = n.getAuto();
            String idAuto = "";
            String marcaAuto = "";
            String modelloAuto = "";

            if (autoCompleta != null && !autoCompleta.isEmpty()) {
                String[] argomenti = autoCompleta.split(" ");

                if (argomenti.length >= 1 && argomenti[0].matches("\\d+")) {
                    idAuto = argomenti[0];

                    if (argomenti.length >= 2) {
                        marcaAuto = argomenti[1];
                    }

                    StringBuilder modelloBuilder = new StringBuilder();
                    for (int i = 2; i < argomenti.length; i++) {
                        modelloBuilder.append(argomenti[i]).append(" ");
                    }
                    modelloAuto = modelloBuilder.toString().trim();
                } else {
                    marcaAuto = autoCompleta;
                }
            }

            SessionSingleton.getInstance().setTempIdNotifica(idAuto);
            SessionSingleton.getInstance().setTempMarca(marcaAuto);
            SessionSingleton.getInstance().setTempModello(modelloAuto);

            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            Stage popupStage = (Stage) source.getScene().getWindow();
            if (popupStage != null) {
                popupStage.close();
            }

            StageHandler.getSingletonInstance().loadPage("/view/GestioneCatalogo.fxml");

        } catch (IOException e) {
            log.error("Errore nell'apertura delle impostazioni", e);
        }
    }
}