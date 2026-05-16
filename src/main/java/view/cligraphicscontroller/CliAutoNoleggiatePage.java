package view.cligraphicscontroller;

import bean.ProfileBean;
import controller.GestioneAutoNoleggiateController;
import model.macchina.Macchina;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.List;

public class CliAutoNoleggiatePage{

    private final GestioneAutoNoleggiateController controller = ControllerFactory.getGraphicalSingletonFactory().createVisualizzaAutoNoleggiateController();

    public void render() {
        boolean back = false;
        while (!back) {
            ConsolePrinter.printHeader("AUTO NOLEGGIATE");

            if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
                ConsolePrinter.printStatus("Devi essere loggato per vedere il garage!", true);
                return;
            }

            ProfileBean bean = new ProfileBean();
            bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());

            try {
                List<Macchina> listaAuto = controller.findCar(bean);

                if (listaAuto == null || listaAuto.isEmpty()) {
                    ConsolePrinter.printStatus("Non hai auto noleggiate al momento.", false);
                } else {
                    mostraTabellaAuto(listaAuto);
                }

                ConsolePrinter.logFormatted("%n[ID] Seleziona auto | [0] Torna alla Home%n");
                String choice = ConsolePrinter.readLine("Scelta > ").trim();

                if ("0".equals(choice)) {
                    back = true;
                } else {
                    gestisciSelezione(choice, listaAuto);
                }

            } catch (Exception e) {
                ConsolePrinter.printStatus("Errore caricamento garage: " + e.getMessage(), true);
                back = true;
            }
        }
    }

    private void mostraTabellaAuto(List<Macchina> lista) {
        String format = "%-4s | %-12s | %-12s | %-8s%n";
        ConsolePrinter.logFormatted(format, "ID", "MARCA", "MODELLO", "SPESA");
        ConsolePrinter.logFormatted("-".repeat(46) + "%n");

        for (Macchina m : lista) {
            ConsolePrinter.logFormatted(format,
                    m.getId(),
                    m.getMarca(),
                    m.getModello(),
                    m.getPrezzo() + " €");
        }
    }

    private void gestisciSelezione(String idInput, List<Macchina> lista) {

        Macchina selezionata = lista.stream()
                .filter(m -> String.valueOf(m.getId()).equals(idInput))
                .findFirst()
                .orElse(null);

        if (selezionata != null) {
            apriOpzioni(selezionata);
        } else {
            ConsolePrinter.printStatus("ID non valido.", true);
        }
    }

    private void apriOpzioni(Macchina macchina) {
        boolean closePopup = false;
        while (!closePopup) {
            ConsolePrinter.printHeader("GESTIONE NOLEGGIO");
            ConsolePrinter.logFormatted("Veicolo: %s %s%n", macchina.getMarca(), macchina.getModello());
            ConsolePrinter.logFormatted("Totale pagato: %.2f €%n", macchina.getPrezzo());
            ConsolePrinter.logFormatted("Stato: Noleggio Attivo%n");
            ConsolePrinter.logFormatted("-".repeat(46) + "%n");

            ConsolePrinter.printMenuOption("1", "Termina Noleggio");
            ConsolePrinter.printMenuOption("0", "Annulla");

            String scelta = ConsolePrinter.readLine("Scelta > ").trim();

            switch (scelta) {
                case "1" -> {
                    try {
                        controller.endRent();
                        ConsolePrinter.printStatus("Noleggio terminato con successo!", false);
                        closePopup = true;
                    } catch (Exception e) {
                        ConsolePrinter.printStatus("Errore chiusura: " + e.getMessage(), true);
                    }
                }
                case "0" -> closePopup = true;
                default -> ConsolePrinter.printStatus("Scelta non valida!", true);
            }
        }
    }
}
