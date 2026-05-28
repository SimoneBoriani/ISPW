package view.cligraphicscontroller;

import bean.NotificaBean;
import bean.ProfileBean;
import controller.GestioneAutoNoleggiateController;
import controller.NotificheController;
import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.List;

public class CliAutoNoleggiatePage {

    private final GestioneAutoNoleggiateController controller = ControllerFactory.getGraphicalSingletonFactory().createGestioneAutoNoleggiateController();
    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public void render() {
        boolean back = false;
        while (!back) {
            ConsolePrinter.printHeader("AUTO NOLEGGIATE");

            if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
                ConsolePrinter.printStatus("Devi essere loggato per vedere le auto noleggiate!", true);
                return;
            }

            ProfileBean bean = new ProfileBean();
            bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());

            try {
                List<NoleggioAuto> listaNoleggi = controller.findRentals(bean);

                if (listaNoleggi == null || listaNoleggi.isEmpty()) {
                    ConsolePrinter.printStatus("Non hai auto noleggiate al momento.", false);
                } else {
                    mostraTabellaNoleggi(listaNoleggi);
                }

                ConsolePrinter.logFormatted("%n[ID NOLEGGIO] Seleziona noleggio | [0] Torna alla Home%n");
                String choice = ConsolePrinter.readLine("Scelta > ").trim();

                if ("0".equals(choice)) {
                    back = true;
                } else {
                    gestisciSelezione(choice, listaNoleggi);
                }

            } catch (Exception e) {
                ConsolePrinter.printStatus("Errore caricamento garage: " + e.getMessage(), true);
                back = true;
            }
        }
    }

    private void mostraTabellaNoleggi(List<NoleggioAuto> lista) {
        String format = "%-11s | %-18s | %-25s | %-10s%n";
        ConsolePrinter.logFormatted(format, "ID_NOLEGGIO", "VEICOLO", "PERIODO", "SPESA_TOT");
        ConsolePrinter.logFormatted("-".repeat(73) + "%n");

        for (NoleggioAuto n : lista) {
            Macchina m = n.getMacchina();
            String veicolo = m.getMarca() + " " + m.getModello();
            if(veicolo.length() > 18) veicolo = veicolo.substring(0, 15) + "...";

            String periodo = n.getDataInizio() + " al " + n.getDataFine();

            ConsolePrinter.logFormatted(format,
                    n.getIdNoleggio(),
                    veicolo,
                    periodo,
                    n.getPrezzoTotalePagato() + " €");
        }
    }

    private void gestisciSelezione(String idInput, List<NoleggioAuto> lista) {

        NoleggioAuto selezionato = lista.stream()
                .filter(n -> String.valueOf(n.getIdNoleggio()).equals(idInput))
                .findFirst()
                .orElse(null);

        if (selezionato != null) {
            apriOpzioni(selezionato);
        } else {
            ConsolePrinter.printStatus("ID Noleggio non valido.", true);
        }
    }

    private void apriOpzioni(NoleggioAuto noleggio) {
        Macchina macchina = noleggio.getMacchina();
        boolean closePopup = false;

        while (!closePopup) {
            ConsolePrinter.printHeader("DETTAGLI NOLEGGIO #" + noleggio.getIdNoleggio());

            ConsolePrinter.logFormatted("Veicolo       : %s %s%n", macchina.getMarca(), macchina.getModello());
            ConsolePrinter.logFormatted("Periodo       : Dal %s al %s%n", noleggio.getDataInizio(), noleggio.getDataFine());
            ConsolePrinter.logFormatted("Totale Pagato : %.2f €%n", noleggio.getPrezzoTotalePagato());
            ConsolePrinter.logFormatted("Trasmissione  : %s %n", macchina.getTrasmissione());
            ConsolePrinter.logFormatted("Alimentazione : %s %n",macchina.getAlimentazione());
            ConsolePrinter.logFormatted("Anno          : %s %n",macchina.getAnno());
            ConsolePrinter.logFormatted("Stato         : %s %n", "ATTIVO");
            ConsolePrinter.logFormatted("-".repeat(50) + "%n");

            ConsolePrinter.printMenuOption("1", "Termina Noleggio Anticipatamente");
            ConsolePrinter.printMenuOption("0", "Annulla e Torna Indietro");

            String scelta = ConsolePrinter.readLine("Scelta > ").trim();

            switch (scelta) {
                case "1" -> {
                    try {
                        controller.endRent(noleggio.getIdNoleggio());

                        NotificaBean nolo = new NotificaBean();
                        nolo.setMacchina(noleggio.getMacchina());
                        nolo.setMsg("Noleggio di "+nolo.getMacchina().getMarca()+" "+nolo.getMacchina().getModello()+" con successo!");
                        notificheController.generaNotificaSistema(nolo);

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