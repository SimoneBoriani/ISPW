package view.cligraphicscontroller;

import bean.SegnalazioneBean;
import controller.NotificheController;
import model.notifiche.Notifica;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.List;
import java.util.Scanner;

public class CliNotifichePage {

    private final NotificheController notificheController= ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public void render() {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            ConsolePrinter.printHeader("Notifiche");
            SegnalazioneBean bean = new SegnalazioneBean();
            bean.setUtente(SessionSingleton.getInstance().getUtenteCorrente());

            List<Notifica> listaNotifiche = notificheController.getStoricoNotifiche(bean);

            if (listaNotifiche == null || listaNotifiche.isEmpty()) {
                System.out.println("\n Nessuna notifica presente al momento.\n");
            } else {
                for (Notifica n : listaNotifiche) {
                    stampaBoxNotifica(n);
                    if (!n.isLetta()) {
                        SegnalazioneBean user = new SegnalazioneBean();
                        user.setId(n.getId());
                        notificheController.apriNotifica(user);
                    }
                }
            }


            System.out.println("=========================================");
            System.out.println("[1] Elimina una notifica (tramite ID)");
            System.out.println("[0] Torna indietro");
            System.out.println("=========================================");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    eliminaNotificaInterattiva(scanner);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Errore: Scelta non valida. Riprova.");
            }
        }
    }

    /**
     * Stampa una singola notifica formattata in un box ASCII
     */
    private void stampaBoxNotifica(Notifica n) {
        String tipoStr = n.getTipo() == Notifica.Tipo.SISTEMA ? "SISTEMA" : "MESSAGGIO";
        String dataStr = n.getDataCreazione().toString().replace("T", " ").substring(0, 16);
        String statoLetta = n.isLetta() ? "(Gia' letta)" : "(NUOVA)";

        System.out.println("+-----------------------------------------------------------------+");
        System.out.printf("| ID: %-5d | Tipo: %-11s | Data: %-16s %-8s |\n",
                n.getId(), tipoStr, dataStr, statoLetta);
        System.out.println("+-----------------------------------------------------------------+");

        String testo = n.getTesto();
        int maxLen = 61;

        for (int i = 0; i < testo.length(); i += maxLen) {
            String riga = testo.substring(i, Math.min(testo.length(), i + maxLen));
            System.out.printf("| %-63s |\n", riga);
        }

        System.out.println("+-----------------------------------------------------------------+\n");
    }

    private void eliminaNotificaInterattiva(Scanner scanner) {
        System.out.print("Inserisci l'ID della notifica da eliminare: ");
        String idInput = scanner.nextLine();

        try {
            int idNotifica = Integer.parseInt(idInput);

            SegnalazioneBean eliminaBean = new SegnalazioneBean();
            eliminaBean.setId(idNotifica);

            notificheController.eliminaNotifica(eliminaBean);

            System.out.println("-> Notifica [" + idNotifica + "] eliminata con successo!");

        } catch (NumberFormatException e) {
            System.out.println("Errore: Devi inserire un numero valido.");
        }
    }
}
