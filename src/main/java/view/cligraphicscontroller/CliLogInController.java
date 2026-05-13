package view.cligraphicscontroller;

import bean.ProfileBean;
import controller.LogInController;
import exceptions.IncorrectCredentialExeption;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

public class CliLogInController {

    private final LogInController logInController = ControllerFactory.getGraphicalSingletonFactory().createLoginController();

    public void render() {
        boolean back = false;
        while (!back) {
            ConsolePrinter.printHeader("Area Login");
            ConsolePrinter.printMenuOption("1", "Accedi");
            ConsolePrinter.printMenuOption("2", "Registrati");
            ConsolePrinter.printMenuOption("0", "Torna al Menu Principale");

            String choice = ConsolePrinter.readLine("Scelta > ").trim();

            switch (choice) {
                case "1" -> auth();
                case "2" -> register();
                case "0" -> back = true;
                default -> ConsolePrinter.printStatus("Scelta non valida!", true);
            }
        }
    }

    private void auth() {

        ConsolePrinter.printHeader("Login - Inserisci Credenziali");

        String user = ConsolePrinter.readLine("Username:").trim();
        char[] passArray = ConsolePrinter.readPassword("Password:");
        String pass = new String(passArray);

        if (user.isEmpty() || pass.isEmpty()) {
            ConsolePrinter.printStatus("Attenzione: Inserisci username e/o password!", true);
            ConsolePrinter.readLine("Premi INVIO per riprovare...");
            return;
        }

        ProfileBean credenziali = new ProfileBean();
        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        try {
            logInController.authenticate(credenziali);

            if (SessionSingleton.getInstance().getUtenteCorrente() != null) {
                String ruolo = SessionSingleton.getInstance().getUtenteCorrente().getRuolo();
                ConsolePrinter.printStatus("Login effettuato! Ruolo: " + ruolo, false);

                if ("USER".equals(ruolo)) {
                    ConsolePrinter.printStatus("Reindirizzamento al Catalogo...", false);
                } else {
                    ConsolePrinter.printStatus("Reindirizzamento Area Admin...", false);
                }
            }
        } catch (IncorrectCredentialExeption e) {
            ConsolePrinter.printStatus(e.getMessage(), true);
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore di sistema, riprova più tardi.", true);
        }
        ConsolePrinter.readLine("\nPremi INVIO per continuare...");
    }

    private void register() {
        ConsolePrinter.printHeader("Registrazione Nuovo Utente");

        String user = ConsolePrinter.readLine("Nuovo Username:").trim();
        String pass = new String(ConsolePrinter.readPassword("Password:"));
        String confPass = new String(ConsolePrinter.readPassword("Conferma Password:"));

        if (user.isEmpty() || pass.isEmpty()) {
            ConsolePrinter.printStatus("Attenzione: Campi obbligatori!", true);
            return;
        }

        if (!pass.equals(confPass)) {
            ConsolePrinter.printStatus("Attenzione: Le password non coincidono!", true);
            return;
        }

        ProfileBean credenziali = new ProfileBean();
        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        if (logInController.researchUser(credenziali)) {
            ConsolePrinter.printStatus("Utente già registrato!", true);
        } else {
            logInController.insert(credenziali);
            ConsolePrinter.printStatus("Registrazione effettuata con successo!", false);
        }
        ConsolePrinter.readLine("\nPremi INVIO per tornare al login...");
    }
}