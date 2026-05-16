package view.cligraphicscontroller;

import controller.VisualizzaStoricoController;
import model.noleggioauto.NoleggioAuto;
import utils.ConsolePrinter;
import view.factory.ControllerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CliVisualizzaStoricoPage {

    private final VisualizzaStoricoController controller = ControllerFactory.getGraphicalSingletonFactory().createVisualizzaStoricoController();

    private static final String ROW_FORMAT = "%-4s | %-25s | %-25s | %-10s | %-10s | %-10s €%n";
    private static final String CMD_BACK = "0";
    private static final String FILTER_ALL = "ALL";
    private static final String FILTER_ATTIVO = "ATTIVO";
    private static final String FILTER_TERMINATO = "TERMINATO";


    public void renderProfitti() {

        boolean backProfitti = false;

        while (!backProfitti) {
            ConsolePrinter.printHeader("GESTIONE FINANZIARIA: REPORT RICAVI");

            try {
                Map<LocalDate, Double> stats = new TreeMap<>(controller.getProfittiPerData());
                double totalProfits = 0;

                ConsolePrinter.logFormatted("--- DETTAGLIO RICAVI GIORNALIERI ---%n");
                for (Map.Entry<LocalDate, Double> entry : stats.entrySet()) {
                    ConsolePrinter.logFormatted("  Data: %s -> Ricavo Giornaliero: %.2f €%n", entry.getKey().toString(), entry.getValue());
                    totalProfits += entry.getValue();
                }
                ConsolePrinter.logFormatted("------------------------------------%n");
                ConsolePrinter.logFormatted("TOTALE PROFITTI ACCUMULATI: %.2f €%n", totalProfits);
                ConsolePrinter.logFormatted("GIORNI DI ATTIVITÀ REGISTRATI: %d%n", stats.size());

            } catch (Exception e) {
                ConsolePrinter.printStatus("Impossibile caricare le statistiche: " + e.getMessage(), true);
            }

            ConsolePrinter.logFormatted("%n");
            ConsolePrinter.printMenuOption(CMD_BACK, "Torna alla Dashboard Principale");
            String choice = ConsolePrinter.readLine("Scelta > ").trim();

            if (CMD_BACK.equals(choice)) {
                backProfitti = true;
            }
        }
    }

    public void renderStorico() {
        boolean backStorico = false;
        String currentFilter = FILTER_ALL;

        while (!backStorico) {
            ConsolePrinter.printHeader("GESTIONE CONTRATTI: REGISTRO STORICO NOLEGGI");

            mostraTabellaNoleggi(currentFilter);
            ConsolePrinter.printMenuOption("1", "Filtra: Mostra solo ATTIVI");
            ConsolePrinter.printMenuOption("2", "Filtra: Mostra solo TERMINATI");
            ConsolePrinter.printMenuOption("3", "Rimuovi filtri (Mostra Tutti)");
            ConsolePrinter.printMenuOption(CMD_BACK, "Torna alla Dashboard Principale");

            String choice = ConsolePrinter.readLine("Scelta > ").trim();

            if (CMD_BACK.equals(choice)) {
                backStorico = true;
            } else if ("1".equals(choice)) {
                currentFilter = FILTER_ATTIVO;
            } else if ("2".equals(choice)) {
                currentFilter = FILTER_TERMINATO;
            } else if ("3".equals(choice)) {
                currentFilter = FILTER_ALL;
            } else {
                ConsolePrinter.printStatus("Scelta non valida!", true);
                ConsolePrinter.readLine("Premi INVIO per continuare...");
            }
        }
    }

    private void mostraTabellaNoleggi(String statusFilter) {
        try {
            List<NoleggioAuto> lista = controller.findRented();

            if (!FILTER_ALL.equals(statusFilter)) {
                lista = lista.stream()
                        .filter(n -> n.getStato().equalsIgnoreCase(statusFilter))
                        .toList();
            }

            ConsolePrinter.logFormatted("CONTRATTI DI NOLEGGIO (Filtro: %s)%n", FILTER_ALL.equals(statusFilter) ? "TUTTI" : statusFilter);
            ConsolePrinter.logFormatted(ROW_FORMAT, "ID", "AUTO", "UTENTE", "STATO", "DATA FINE", "PREZZO");
            ConsolePrinter.logFormatted("-".repeat(78) + "%n");

            if (lista.isEmpty()) {
                ConsolePrinter.printStatus("Nessun noleggio corrispondente al filtro applicato.", false);
            } else {
                renderCicloRigheTabella(lista);
            }
            ConsolePrinter.logFormatted("-".repeat(78) + "%n");

        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore nel caricamento della tabella noleggi: " + e.getMessage(), true);
        }
    }

    private void renderCicloRigheTabella(List<NoleggioAuto> lista) {
        for (NoleggioAuto n : lista) {
            String endDateStr = n.getDataFine() != null ? n.getDataFine().toString() : "N/D";
            String infoAuto = n.getMacchina().getMarca() + " " + n.getMacchina().getModello();
            String displayStatus = FILTER_ATTIVO.equalsIgnoreCase(n.getStato()) ? FILTER_ATTIVO : FILTER_TERMINATO;

            ConsolePrinter.logFormatted(ROW_FORMAT,
                    n.getMacchina().getId(),
                    infoAuto.length() > 25 ? infoAuto.substring(0, 20) + "..." : infoAuto,
                    n.getUtente().getUsername(),
                    displayStatus,
                    endDateStr,
                    String.format("%.2f", n.getPrezzoTotalePagato())
            );
        }
    }
}