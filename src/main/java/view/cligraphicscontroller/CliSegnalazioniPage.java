package view.cligraphicscontroller;

import bean.NotificaBean;
import controller.NotificheController;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

public class CliSegnalazioniPage {

    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public void render() {

        ConsolePrinter.printHeader("Segnala un Problema");
        ConsolePrinter.logFormatted("Descrivi il problema riscontrato (non superare i 100 caratteri).");
        ConsolePrinter.printMenuOption("0", "Torna indietro");

        String messaggio = leggiMessaggio();

        if (messaggio == null) {
            ConsolePrinter.logFormatted("Segnalazione annullata.");
            return;
        }

        inviaSegnalazione(messaggio);
    }

    private String leggiMessaggio() {

        while (true) {

            String messaggio = ConsolePrinter.readLine("Messaggio > ").trim();

            if (messaggio.equals("0")) {
                return null;
            }

            if (messaggio.isEmpty() || messaggio.length() > 100) {
                ConsolePrinter.logFormatted("Errore: Formato del messaggio non valido. Riprova.");
                continue;
            }

            return messaggio;
        }
    }

    private void inviaSegnalazione(String messaggio) {

        var session = SessionSingleton.getInstance();

        NotificaBean segnalazione = new NotificaBean();
        segnalazione.setUtente(session.getUtenteCorrente());
        segnalazione.setMacchina(session.getAutoSelezionata());
        segnalazione.setMsg(messaggio);

        notificheController.inviaMessaggioAdAdmin(segnalazione);
    }
}
