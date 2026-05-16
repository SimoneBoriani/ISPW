package view.cligraphicscontroller;

import bean.NoleggioAutoBean;
import controller.NoleggioController;
import model.duratacontrattuale.PianoNoleggio;
import model.macchina.Macchina;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

public class CliNoleggioPage {

    private static final String FORMAT= "%-15s : %s%n";
    private final NoleggioController noleggioController= ControllerFactory.getGraphicalSingletonFactory().createNoleggioController();

    public void render(){

        boolean back=false;

        while(!back){

        ConsolePrinter.printHeader("Noleggio Auto");

        ConsolePrinter.logFormatted(FORMAT,"Modello:", SessionSingleton.getInstance().getAutoSelezionata().getModello());
        ConsolePrinter.logFormatted(FORMAT,"Marca:",SessionSingleton.getInstance().getAutoSelezionata().getMarca());
        ConsolePrinter.logFormatted(FORMAT,"Prezzo:",SessionSingleton.getInstance().getAutoSelezionata().getPrezzo());
        ConsolePrinter.logFormatted(FORMAT,"Posti:",SessionSingleton.getInstance().getAutoSelezionata().getPosti());
        ConsolePrinter.logFormatted(FORMAT,"Alimentazione:",SessionSingleton.getInstance().getAutoSelezionata().getAlimentazione());
        ConsolePrinter.logFormatted(FORMAT,"Trasmissione:",SessionSingleton.getInstance().getAutoSelezionata().getTrasmissione());
        ConsolePrinter.logFormatted(FORMAT,"Tipologia:",SessionSingleton.getInstance().getAutoSelezionata().getTipologia());

        ConsolePrinter.printMenuOption("1","Procedi al noleggio");
        ConsolePrinter.printMenuOption("0","Torna indietro");

        String choice = ConsolePrinter.readLine("Selezione >").trim();

        switch (choice) {

            case "1" -> {
                NoleggioAutoBean bean = new NoleggioAutoBean();
                bean.setMacchina(SessionSingleton.getInstance().getAutoSelezionata());
                bean.setRenter(SessionSingleton.getInstance().getUtenteCorrente());
                configuraNoleggio(bean);
            }

            case "0" ->back=true;
            default -> {/*Ricarica pagina*/}
        }
        }
    }

    private void configuraNoleggio(NoleggioAutoBean bean) {

        boolean confirmed = false;

        while (!confirmed) {
            ConsolePrinter.printHeader("CONFIGURA NOLEGGIO");

            printScontrino(bean.getMacchina(), bean.getGiorni());

            ConsolePrinter.printMenuOption("1","Inserisci/Cambia Giorni");
            ConsolePrinter.printMenuOption("2","CONFERMA NOLEGGIO");
            ConsolePrinter.printMenuOption("0","Annulla");

            String choice = ConsolePrinter.readLine("Scelta > ").trim();

            switch (choice) {

                case "1" -> {
                    String input = ConsolePrinter.readLine("Quanti giorni? ");
                    try {
                        bean.setGiorni(Integer.parseInt(input));
                        if (bean.getGiorni() <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        ConsolePrinter.printStatus("Inserisci un numero di giorni valido!", true);
                        bean.setGiorni(0);
                    }
                }
                case "2" -> {
                    if (bean.getGiorni() > 0) {
                        gestisciConferma(bean);
                        confirmed = true;
                    } else {
                        ConsolePrinter.printStatus("Devi prima inserire i giorni!", true);
                    }
                }

                case "0" -> confirmed = true;
                default -> ConsolePrinter.printStatus("Scelta non valida!", true);
            }
        }
    }

    private void printScontrino(Macchina auto, int giorni) {
        ConsolePrinter.logFormatted("--- RIEPILOGO COSTI ---%n");
        ConsolePrinter.logFormatted("Auto: %s %s%n", auto.getMarca(), auto.getModello());

        if (giorni > 0) {
            try {
                PianoNoleggio piano = noleggioController.determinaPiano(giorni);
                double totale = noleggioController.calcolaTotale(auto, giorni);

                ConsolePrinter.logFormatted("Durata: %d giorni%n", giorni);
                ConsolePrinter.logFormatted("Piano:  %s%n", piano.getDescrizione());
                ConsolePrinter.logFormatted("TOTALE: %.2f €%n", totale);
            } catch (Exception e) {
                ConsolePrinter.logFormatted("Errore calcolo: %s%n", e.getMessage());
            }
        } else {
            ConsolePrinter.logFormatted("Piano: Inserisci i giorni...%n");
            ConsolePrinter.logFormatted("Totale: 0,00 €%n");
        }
        ConsolePrinter.logFormatted("-----------------------%n");
    }

    private void gestisciConferma(NoleggioAutoBean bean) {

        if (!SessionSingleton.getInstance().isUserLoggedIn()) {
            ConsolePrinter.printStatus("Esegui l'accesso per noleggiare!", true);
            ConsolePrinter.readLine("Premi ENTER per il login...");
            new CliLogInPage().render();
        }

        try {

            noleggioController.processaNoleggio(bean);
            ConsolePrinter.printStatus("Noleggio completato con successo!", false);
            ConsolePrinter.readLine("Premi ENTER per tornare...");

        } catch (Exception e) {

            ConsolePrinter.printStatus("Errore: " + e.getMessage(), true);
            ConsolePrinter.readLine("Premi ENTER...");

        }
    }
}