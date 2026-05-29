package view.cligraphicscontroller;

import bean.NotificaBean;
import bean.ProfileBean;
import controller.GestioneProfiloController;
import controller.RicaricaController;
import controller.NotificheController;
import model.utente.Utente;
import service.StripeService;
import utils.ConfigLoader;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

public class CliProfilePage {

    private static final String FORMAT="  %-15s : %s%n";
    private static final String INVIOL="Premi INVIO per tornare al profilo...";
    private final GestioneProfiloController gestioneProfiloController = ControllerFactory.getGraphicalSingletonFactory().createGestioneProfiloController();
    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public void render() {

        ConsolePrinter.printHeader("IL TUO PROFILO");

        Utente utenteLoggato = SessionSingleton.getInstance().getUtenteCorrente();

        if (utenteLoggato == null) {
            ConsolePrinter.logFormatted("%n  Dati Account:%n");
            ConsolePrinter.logFormatted(FORMAT, "USERNAME", "Guest");
            ConsolePrinter.logFormatted(FORMAT, "NOME", "N/D");
            ConsolePrinter.logFormatted(FORMAT, "COGNOME", "N/D");
            ConsolePrinter.logFormatted(FORMAT, "SALDO", "0.0 €");
            ConsolePrinter.logFormatted(FORMAT, "RUOLO", "GUEST");
            ConsolePrinter.logFormatted(FORMAT, "PATENTE", "Non Verificata");
        } else {
            String statoPatente = Boolean.TRUE.equals(utenteLoggato.getVerificato()) ? "VERIFICATA \u2714" : "Non Verificata \u274C";

            ConsolePrinter.logFormatted("%n  Dati Account:%n");
            ConsolePrinter.logFormatted(FORMAT, "USERNAME", utenteLoggato.getUsername());
            ConsolePrinter.logFormatted(FORMAT, "NOME", utenteLoggato.getNome());
            ConsolePrinter.logFormatted(FORMAT, "COGNOME", utenteLoggato.getCognome());
            ConsolePrinter.logFormatted(FORMAT, "SALDO", utenteLoggato.getSaldo() + " €");
            ConsolePrinter.logFormatted(FORMAT, "RUOLO", utenteLoggato.getRuolo());
            ConsolePrinter.logFormatted(FORMAT, "PATENTE", statoPatente);
        }

        while (true) {
            ConsolePrinter.logFormatted("%n");
            ConsolePrinter.printMenuOption("1","Aggiungi saldo (Checkout Stripe)");
            ConsolePrinter.printMenuOption("2","Aggiorna informazioni personali");
            ConsolePrinter.printMenuOption("3","Aggiorna Password");
            ConsolePrinter.printMenuOption("4","Verifica Patente (Identity Stripe)");
            ConsolePrinter.printMenuOption("0","Torna indietro");

            String scelta = ConsolePrinter.readLine("Scelta > ").trim();

            switch (scelta) {
                case "1" -> {
                    if (SessionSingleton.getInstance().isUserLoggedIn()) {
                        addSaldo();
                        render();
                        return;
                    } else guest();
                }

                case "2" -> {
                    if (SessionSingleton.getInstance().isUserLoggedIn()) {
                        updateInfo();
                        render();
                        return;
                    } else guest();
                }

                case "3" -> {
                    ConsolePrinter.logFormatted("Modulo da implementare");
                    ConsolePrinter.readLine("Premi ENTER per continuare ...");
                }

                case "4" -> {
                    if (SessionSingleton.getInstance().isUserLoggedIn()) {
                        verificaPatente();
                        render();
                        return;
                    } else guest();
                }

                case "0" -> {
                    return;
                }
                default -> ConsolePrinter.printStatus("Scelta non valida!", true);
            }
        }
    }

    private void addSaldo() {
        ConsolePrinter.printHeader("RICARICA CONTO - STRIPE API");
        ConsolePrinter.logFormatted("Inserisci l'importo da ricaricare e verrai reindirizzato al portale di Stripe.%n");

        String importoStr = ConsolePrinter.readLine("Importo (es. 15.50): €");

        try {
            double importo = Double.parseDouble(importoStr.replace(",", "."));
            if (importo <= 0) {
                ConsolePrinter.printStatus("L'importo deve essere positivo!", true);
                ConsolePrinter.readLine("Premi ENTER per tornare indietro ...");
                return;
            }

            ConsolePrinter.printStatus("Apertura del browser in corso...", false);
            ConsolePrinter.logFormatted("Completa il pagamento sulla pagina Stripe e poi torna su questa console.%n");

            StripeService stripeService = new StripeService(
                    ConfigLoader.get("stripe.secret.key"),
                    ConfigLoader.getInt("stripe.success.port")
            );
            RicaricaController ricaricaController = new RicaricaController(stripeService);

            boolean ricaricaRiuscita = ricaricaController.ricaricaSaldo(importo);

            if (ricaricaRiuscita) {
                ConsolePrinter.printStatus("Ricarica completata con successo! Aggiunti " + importo + "€.", false);

                NotificaBean notifica = new NotificaBean();
                notifica.setUtente(SessionSingleton.getInstance().getUtenteCorrente());
                notifica.setMsg("Importo di " + importo + "€ depositato con successo!");
                notificheController.generaNotificaSistema(notifica);

            } else {
                ConsolePrinter.printStatus("Operazione annullata o fallita.", true);
            }

        } catch (NumberFormatException e) {
            ConsolePrinter.printStatus("Errore: Inserisci un valore numerico valido.", true);
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore durante la ricarica: " + e.getMessage(), true);
        }

        ConsolePrinter.readLine(INVIOL);
    }

    private void verificaPatente() {
        Utente utente = SessionSingleton.getInstance().getUtenteCorrente();

        if (Boolean.TRUE.equals(utente.getVerificato())) {
            ConsolePrinter.printStatus("La tua patente è già stata verificata con successo!", false);
            ConsolePrinter.readLine(INVIOL);
            return;
        }

        ConsolePrinter.printHeader("VERIFICA IDENTITÀ - STRIPE IDENTITY");
        ConsolePrinter.logFormatted("Apertura del browser in corso per la scansione del documento...%n");
        ConsolePrinter.logFormatted("Segui le istruzioni sullo schermo per caricare la foto della tua Patente.%n");

        try {

            ProfileBean bean = new ProfileBean();
            bean.setId(utente.getIdUser());
            String statoVerifica = gestioneProfiloController.avviaVerificaPatente(bean);

            if ("verified".equals(statoVerifica)) {
                gestioneProfiloController.completaVerificaPatente(bean);
                ConsolePrinter.printStatus("Patente validata! Ora sei abilitato al noleggio.", false);
            } else if ("processing".equals(statoVerifica)) {
                ConsolePrinter.printStatus("Verifica in corso. Stripe sta processando i tuoi documenti. Riprova più tardi.", true);
            } else if ("requires_input".equals(statoVerifica)) {
                ConsolePrinter.printStatus("Verifica fallita. Riprova assicurandoti che l'immagine sia chiara e leggibile.", true);
            } else {
                ConsolePrinter.printStatus("Operazione di verifica annullata.", true);
            }

        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore durante il collegamento a Stripe: " + e.getMessage(), true);
        }

        ConsolePrinter.readLine(INVIOL);
    }

    private void guest() {
        ConsolePrinter.printStatus("Accesso richiesto per l'operazione.", false);
        ConsolePrinter.readLine("Premi INVIO per andare al Login...");
        new CliLogInPage().render();
    }

    private void updateInfo() {

        ConsolePrinter.printHeader("Aggiorna dati Personali");
        ConsolePrinter.logFormatted("Premi Invio per mantenere i dati attuali.");

        String nome = ConsolePrinter.readLine("Nuovo Nome:");
        String cognome = ConsolePrinter.readLine("Nuovo Cognome:");

        ProfileBean bean = new ProfileBean();
        bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());

        Utente u = SessionSingleton.getInstance().getUtenteCorrente();
        bean.setNome(!nome.isBlank() ? nome : u.getNome());
        bean.setCognome(!cognome.isBlank() ? cognome : u.getCognome());
        bean.setUsername(u.getUsername());

        gestioneProfiloController.updateProfile(bean);
        ConsolePrinter.printStatus("Informazioni aggiornate correttamente!", false);
        ConsolePrinter.readLine(INVIOL);
    }
}