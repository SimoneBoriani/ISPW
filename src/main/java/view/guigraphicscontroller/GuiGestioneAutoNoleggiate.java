package view.guigraphicscontroller;

import bean.ProfileBean;
import controller.GestioneAutoNoleggiateController;
import exceptions.GenericSystemException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.macchina.Macchina;
import utils.SessionSingleton;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.util.List;

public class GuiGestioneAutoNoleggiate {

    private GestioneAutoNoleggiateController visualizzaAutoNoleggiateController=ControllerFactory.getGraphicalSingletonFactory().createVisualizzaAutoNoleggiateController();

    @FXML
    private ListView<Macchina> carListView;

    public void getCars(){

        ProfileBean bean=new ProfileBean();
        bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());
        try {
            if (carListView != null) {

                List<Macchina> listaAuto = visualizzaAutoNoleggiateController.findCar(bean);

                if (listaAuto != null && !listaAuto.isEmpty()) {
                    ObservableList<Macchina> data = FXCollections.observableArrayList(listaAuto);
                    carListView.setItems(data);
                    carListView.setCellFactory(param -> new CarCell());

                    carListView.setOnMouseClicked(event -> {
                        Macchina selezionata = carListView.getSelectionModel().getSelectedItem();
                        if (selezionata != null) {
                            apriOpzioni(selezionata);
                        }
                    });
                }
            }
        } catch (Exception e) {
            throw new GenericSystemException("Errore Caricamento", e);
        }
    }

    private void apriOpzioni(Macchina macchina) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Gestione Noleggio");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ffffff;");

        Label lblTitolo = new Label(macchina.getMarca() + " " + macchina.getModello());
        lblTitolo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Label lblSpesa = new Label("Totale pagato: " + macchina.getPrezzo() + " €");
        lblSpesa.setStyle("-fx-font-size: 14px;");

        Label lblInfo = new Label("Stato: Noleggio Attivo");
        lblInfo.setStyle("-fx-text-fill: #555555;");

        Button btnTermina = new Button("Termina Noleggio");
        btnTermina.getStyleClass().add("Button");
        btnTermina.setMinWidth(200);

        btnTermina.setOnAction(e -> {
            try {

                visualizzaAutoNoleggiateController.endRent();
                getCars();

                popupStage.close();
            } catch (Exception ex) {
                throw new GenericSystemException("Errore durante la chiusura anticipata", ex);
            }
        });

        layout.getChildren().addAll(lblTitolo, lblSpesa, lblInfo, btnTermina);

        Scene scene = new Scene(layout, 350, 250);

        StageHandler.getSingletonInstance().loadCss(scene);

        popupStage.setResizable(false);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    public void initialize(){

        if(SessionSingleton.getInstance().getUtenteCorrente()!=null) {
                getCars();
        }
    }

    @FXML
    public void goToHome(MouseEvent event) throws IOException {
        String str="/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }


}
