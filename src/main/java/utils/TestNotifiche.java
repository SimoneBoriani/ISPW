package utils;

import model.notifiche.Notifica;
import model.notifiche.dao.DbmsDaoNotifica;

import java.util.List;

public class TestNotifiche {

    public static void main(String[] args) {

        System.out.println("--- INIZIO TEST NOTIFICHE ---");

        // DAO
        DbmsDaoNotifica dao = new DbmsDaoNotifica();

        /*
            USER ID = 9
         */
        String userId = "9";

        /*
            CREAZIONE NOTIFICHE TEST
         */

        Notifica n1 = new Notifica(
                "SISTEMA",
                userId,
                "Benvenuto su Boro Rental!",
                Notifica.Tipo.SISTEMA
        );

        Notifica n2 = new Notifica(
                "MESSAGGIO",
                userId,
                "Hai ricevuto una nuova prenotazione.",
                Notifica.Tipo.MESSAGGIO
        );

        Notifica n3 = new Notifica(
                "SISTEMA",
                userId,
                "Il pagamento è stato confermato.",
                Notifica.Tipo.SISTEMA
        );

        /*
            SALVATAGGIO
         */
        System.out.println("Inserimento notifiche...");

        dao.inserisci(n1);
        dao.inserisci(n2);
        dao.inserisci(n3);

        System.out.println("Notifiche salvate!");

        /*
            RECUPERO NOTIFICHE NON LETTE
         */
        System.out.println("\nNotifiche NON lette per user ID = 9");

        List<Notifica> notifiche =
                dao.getNonLette(userId);

        if (notifiche.isEmpty()) {

            System.out.println("Nessuna notifica trovata.");

        } else {

            for (Notifica n : notifiche) {

                System.out.println("--------------------------------");

                System.out.println("ID: " + n.getId());

                System.out.println("Tipo: " + n.getTipo());

                System.out.println("Testo: " + n.getTesto());

                System.out.println("Letta: " + n.isLetta());

                System.out.println("--------------------------------");
            }
        }

        System.out.println("\n--- FINE TEST ---");
    }
}