package view.guigraphicscontroller;

import controller.VisualizzaStoricoController;
import exceptions.GenericSystemException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import model.noleggioauto.NoleggioAuto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.StageHandler;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GuiVisualizzaStorico {

    private final VisualizzaStoricoController controller = ControllerFactory.getGraphicalSingletonFactory().createVisualizzaStoricoController();

    private final Logger logger= LogManager.getLogger(GuiVisualizzaStorico.class);

    @FXML
    private Label lblIndietro;

    @FXML
    private ComboBox<String> comboStatoNoleggi;

    @FXML
    private TableView<NoleggioAuto> tblNoleggi;

    @FXML
    private TableColumn<NoleggioAuto, String> colAuto;

    @FXML
    private TableColumn<NoleggioAuto, String> colUtente;

    @FXML
    private TableColumn<NoleggioAuto, String> colStato;

    @FXML
    private TableColumn<NoleggioAuto, String> colDataFine;

    @FXML
    private TableColumn<NoleggioAuto, String> colMotivoFine;

    @FXML
    private TableColumn<NoleggioAuto, Double> colPrezzo;

    @FXML
    private Label lblTotaleProfitti;

    @FXML
    private Label lblNoleggiConclusi;

    @FXML
    private LineChart<String, Number> chartProfitti;

    @FXML
    public void initialize() {
        setupListeners();

        if (tblNoleggi != null) {
            configureTable();
            refreshTable();
        }

        if (chartProfitti != null) {
            caricaStatistiche();
        }
    }

    private void configureTable() {
        colAuto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMacchina().getMarca() + " " + c.getValue().getMacchina().getModello()));
        colUtente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUtente().getUsername()));
        colStato.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStato()));
        colPrezzo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrezzoTotalePagato()));
        colDataFine.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataFine() != null ? c.getValue().getDataFine().toString() : "N/D"));

        if (colMotivoFine != null) {
            colMotivoFine.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotivoChiusura()));
        }

        if (comboStatoNoleggi != null) {
            comboStatoNoleggi.getItems().setAll("Tutti", "ATTIVO", "TERMINATO");
            comboStatoNoleggi.getSelectionModel().selectFirst();
        }
    }

    private void caricaStatistiche() {
        try {
            Map<LocalDate, Double> stats = new TreeMap<>(controller.getProfittiPerData());
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ricavi (€)");

            double totale = 0;
            for (Map.Entry<LocalDate, Double> entry : stats.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
                totale += entry.getValue();
            }

            chartProfitti.getData().setAll(series);
            if (lblTotaleProfitti != null) lblTotaleProfitti.setText(String.format("€ %.2f", totale));
            if (lblNoleggiConclusi != null) lblNoleggiConclusi.setText(String.valueOf(stats.size()));
        } catch (Exception e) {
            throw new GenericSystemException("Errore statistiche: " + e.getMessage());
        }
    }

    private void setupListeners() {
        if (lblIndietro != null) {
            lblIndietro.setOnMouseClicked(e -> goToHome());
        }
        if (comboStatoNoleggi != null) {
            comboStatoNoleggi.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> applicaFiltro(newVal));
        }
    }

    private void refreshTable() {
        tblNoleggi.setItems(FXCollections.observableArrayList(controller.findRented()));
    }

    private void applicaFiltro(String stato) {
        List<NoleggioAuto> lista = controller.findRented();
        if (!stato.equals("Tutti")) {
            lista = lista.stream().filter(n -> n.getStato().equalsIgnoreCase(stato)).toList();
        }
        tblNoleggi.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML private void goToHome() {
        try { StageHandler.getSingletonInstance().loadPage("/view/AdminView.fxml"); }
        catch (IOException e) { logger.error(e); }
    }
}