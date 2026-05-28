package view.cligraphicscontroller;

import bean.NotificaBean;
import controller.NotificheController;
import model.notifiche.Notifica;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.List;

public class CliNotifichePage {

    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public void render() {
        boolean running = true;

        while (running) {
            ConsolePrinter.printHeader("Notifiche");
            NotificaBean bean = new NotificaBean();
            bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());

            List<Notifica> listaNotifiche = notificheController.getStoricoNotifiche(bean);

            if (listaNotifiche == null || listaNotifiche.isEmpty()) {
                ConsolePrinter.printStatus("Nessuna notifica presente al momento.", false);
            } else {
                for (Notifica n : listaNotifiche) {
                    stampaBoxNotifica(n);
                    if (!n.isLetta()) {
                        NotificaBean user = new NotificaBean();
                        user.setId(String.valueOf(n.getId()));
                        notificheController.apriNotifica(user);
                    }
                }
            }

            ConsolePrinter.logFormatted("%n=========================================%n");
            ConsolePrinter.printMenuOption("D", "Elimina una notifica (tramite ID)");
            ConsolePrinter.printMenuOption("0", "Torna indietro");
            ConsolePrinter.logFormatted("=========================================%n");

            String scelta = ConsolePrinter.readLine("Scelta:").toUpperCase();

            switch (scelta) {
                case "D":
                    eliminaNotificaInterattiva();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    ConsolePrinter.printStatus("Scelta non valida. Riprova.", true);
            }
        }
    }

    private void stampaBoxNotifica(Notifica n) {
        String tipoStr = n.getTipo() == Notifica.Tipo.SISTEMA ? "SISTEMA" : "MESSAGGIO";
        String dataStr = n.getDataCreazione().toString().replace("T", " ").substring(0, 16);
        String statoLetta = n.isLetta() ? "(Gia' letta)" : "(NUOVA)";

        ConsolePrinter.logFormatted("+-----------------------------------------------------------------+%n");
        ConsolePrinter.logFormatted("| ID: %-5d | Tipo: %-11s | Data: %-16s %-8s |%n",
                n.getId(), tipoStr, dataStr, statoLetta);
        ConsolePrinter.logFormatted("+-----------------------------------------------------------------+%n");

        String testo = n.getTesto();
        int maxLen = 61;

        for (int i = 0; i < testo.length(); i += maxLen) {
            String riga = testo.substring(i, Math.min(testo.length(), i + maxLen));
            ConsolePrinter.logFormatted("| %-63s |%n", riga);
        }

        ConsolePrinter.logFormatted("+-----------------------------------------------------------------+%n%n");
    }

    private void eliminaNotificaInterattiva() {
        String idInput = ConsolePrinter.readLine("Inserisci l'ID della notifica da eliminare:");

        try {
            int idNotifica = Integer.parseInt(idInput);

            NotificaBean eliminaBean = new NotificaBean();
            eliminaBean.setId(String.valueOf(idNotifica));

            notificheController.eliminaNotifica(eliminaBean);

            ConsolePrinter.printStatus("Notifica [" + idNotifica + "] eliminata con successo!", false);

        } catch (NumberFormatException e) {
            ConsolePrinter.printStatus("Devi inserire un numero valido.", true);
        }
    }
}