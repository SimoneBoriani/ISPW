package view.cligraphicscontroller;

import bean.ProfileBean;
import controller.GestioneProfiloController;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

public class CliProfilePage {

    private static final String FORMAT="  %-15s : %s%n";
    private GestioneProfiloController gestioneProfiloController= ControllerFactory.getGraphicalSingletonFactory().createGestioneProfiloController();

    public void render() {

        ConsolePrinter.printHeader("IL TUO PROFILO");

        var utenteLoggato = SessionSingleton.getInstance().getUtenteCorrente();


        if (utenteLoggato == null) {

            ConsolePrinter.logFormatted("%n  Dati Account:%n");
            ConsolePrinter.logFormatted(FORMAT, "USERNAME", "Guest");
            ConsolePrinter.logFormatted(FORMAT, "NOME", "N/D");
            ConsolePrinter.logFormatted(FORMAT, "COGNOME", "N/D");
            ConsolePrinter.logFormatted(FORMAT, "SALDO", 0.0);
            ConsolePrinter.logFormatted(FORMAT, "RUOLO", "GUEST");

        } else {

            ConsolePrinter.logFormatted("%n  Dati Account:%n");
            ConsolePrinter.logFormatted(FORMAT, "USERNAME", utenteLoggato.getUsername());
            ConsolePrinter.logFormatted(FORMAT, "NOME", utenteLoggato.getNome());
            ConsolePrinter.logFormatted(FORMAT, "COGNOME", utenteLoggato.getCognome());
            ConsolePrinter.logFormatted(FORMAT, "SALDO", utenteLoggato.getSaldo());
            ConsolePrinter.logFormatted(FORMAT, "RUOLO", utenteLoggato.getRuolo());

        }

        while (true) {

            ConsolePrinter.printMenuOption("1","Aggiungi saldo");
            ConsolePrinter.printMenuOption("2","Aggiorna informazioni personali");
            ConsolePrinter.printMenuOption("3","Aggiorna Password");
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
                    }else guest();
                }

                case "3"-> {
                    ConsolePrinter.logFormatted("Modulo da implementare");
                    ConsolePrinter.readLine("Premi ENTER per continuare ...");
                }
                case "0" -> {
                    return;
                }
                default -> ConsolePrinter.printStatus("Scelta non valida!", true);
            }
        }
    }

    private void addSaldo() {

        ConsolePrinter.printHeader("RICARICA CONTO");

        ConsolePrinter.logFormatted("  Inserisci i dati della carta %n");
        ConsolePrinter.logFormatted("  Numero Carta:xxxx xxxx xxxx");
        ConsolePrinter.logFormatted("  Scadenza (MM/AA):xx/yy");
        ConsolePrinter.logFormatted("  CVV:xyz");

        String importoStr = ConsolePrinter.readLine("  Importo: "+"€");

        try {

            double importo = Double.parseDouble(importoStr);

            if (importo <= 0) {
                ConsolePrinter.printStatus("L'importo deve essere positivo!", true);
                ConsolePrinter.readLine("Premi ENTER per continuare ...");
                return;
            }

            ProfileBean bean = new ProfileBean();

            bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());
            bean.setSaldo(importo);

            gestioneProfiloController.updateProfile(bean);

            ConsolePrinter.printStatus("Ricarica completata con successo!", false);

        } catch (NumberFormatException e) {
            ConsolePrinter.printStatus("Errore: Inserisci un valore numerico valido.", true);
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore durante l'aggiornamento: " + e.getMessage(), true);
        }

        ConsolePrinter.readLine("Premi INVIO per tornare al profilo...");
    }

    private void guest() {
        ConsolePrinter.printStatus("Accesso richiesto per l'operazione.", false);
        ConsolePrinter.readLine("Premi INVIO per andare al Login...");
        new CliLogInPage().render();
    }

    private void updateInfo(){

        ConsolePrinter.printHeader("Aggiorna dati Personali");

        ConsolePrinter.logFormatted("Username:");
        String nome=ConsolePrinter.readLine("NOME:");
        String cognome=ConsolePrinter.readLine("Cognome:");

        ProfileBean bean = new ProfileBean();

        bean.setId(SessionSingleton.getInstance().getUtenteCorrente().getIdUser());
        bean.setNome(nome);
        bean.setCognome(cognome);

        gestioneProfiloController.updateProfile(bean);

    }
}