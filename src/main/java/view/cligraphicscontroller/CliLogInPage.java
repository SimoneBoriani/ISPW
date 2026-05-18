package view.cligraphicscontroller;

import bean.ProfileBean;
import controller.LogInController;
import exceptions.IncorrectCredentialExeption;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

public class CliLogInPage {

    private final LogInController logInController = ControllerFactory.getGraphicalSingletonFactory().createLoginController();
    private static final String MSG = "Premi INVIO per riprovare...";

    public void render() {
        boolean back = false;
        while (!back && !SessionSingleton.getInstance().isUserLoggedIn()) {
            ConsolePrinter.printHeader("Area Login");
            ConsolePrinter.printMenuOption("1", "Accedi");
            ConsolePrinter.printMenuOption("2", "Registrati");
            ConsolePrinter.printMenuOption("0", "Torna al Menu Principale");

            String choice = ConsolePrinter.readLine("Scelta > ").trim();

            if ("1".equals(choice)) {
                auth();
            } else if ("2".equals(choice)) {
                register();
            } else if ("0".equals(choice)) {
                back = true;
            } else {
                ConsolePrinter.printStatus("Scelta non valida!", true);
            }
        }
    }

    private void auth() {
        boolean loginTerminato = false;

        while (!loginTerminato && !SessionSingleton.getInstance().isUserLoggedIn()) {
            ConsolePrinter.printHeader("Login - Inserisci Credenziali");
            ConsolePrinter.printStatus("Inserisci le credenziali o premi 0 su Username per tornare indietro.", false);

            String user = ConsolePrinter.readLine("Username:").trim();

            if ("0".equals(user)) {
                loginTerminato = true;
            } else {
                loginTerminato = eseguiTentativoAuth(user);
            }
        }
    }

    private boolean eseguiTentativoAuth(String user) {
        char[] passArray = ConsolePrinter.readPassword("Password:");
        String pass = new String(passArray);

        if (user.isEmpty() || pass.isEmpty()) {
            ConsolePrinter.printStatus("Attenzione: Inserisci username e/o password!", true);
            ConsolePrinter.readLine(MSG);
            return false;
        }

        ProfileBean credenziali = new ProfileBean();
        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        return processaAutenticazioneController(credenziali);
    }

    private boolean processaAutenticazioneController(ProfileBean credenziali) {
        try {
            logInController.authenticate(credenziali);

            if (SessionSingleton.getInstance().getUtenteCorrente() != null) {
                String ruolo = SessionSingleton.getInstance().getUtenteCorrente().getRuolo();
                ConsolePrinter.printStatus("Login effettuato! Ruolo: " + ruolo, false);

                if (!"USER".equals(ruolo)) {
                    ConsolePrinter.printStatus("Reindirizzamento Area Admin...", false);
                    ConsolePrinter.readLine("\n"+"Premi INVIO ...");
                    new CliAdminPage().render();
                } else {
                    ConsolePrinter.printStatus("Reindirizzamento in corso...", false);
                    ConsolePrinter.readLine("\n"+"Premi INVIO...");
                }
                return true;
            }
        } catch (IncorrectCredentialExeption e) {
            ConsolePrinter.printStatus(e.getMessage() + " Riprova o premi 0 per uscire.", true);
            ConsolePrinter.readLine("Premi INVIO per ricaricare la pagina...");
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore di sistema, riprova più tardi.", true);
            ConsolePrinter.readLine("Premi INVIO...");
            return true;
        }
        return false;
    }

    private void register() {
        boolean regTerminato = false;

        while (!regTerminato) {
            ConsolePrinter.printHeader("Registrazione Nuovo Utente");
            ConsolePrinter.printStatus("Inserisci i dati o premi 0 su Nuovo Username per annullare.", false);

            String user = ConsolePrinter.readLine("Nuovo Username:").trim();

            if ("0".equals(user)) {
                regTerminato = true;
            } else {
                regTerminato = eseguiTentativoRegistrazione(user);
            }
        }
    }

    private boolean eseguiTentativoRegistrazione(String user) {
        String pass = new String(ConsolePrinter.readPassword("Password:"));
        String confPass = new String(ConsolePrinter.readPassword("Conferma Password:"));

        if (user.isEmpty() || pass.isEmpty()) {
            ConsolePrinter.printStatus("Attenzione: Campi obbligatori!", true);
            ConsolePrinter.readLine(MSG);
            return false;
        }

        if (!pass.equals(confPass)) {
            ConsolePrinter.printStatus("Attenzione: Le password non coincidono!", true);
            ConsolePrinter.readLine(MSG);
            return false;
        }

        ProfileBean credenziali = new ProfileBean();
        credenziali.setUsername(user);
        credenziali.setPassword(pass);

        return processaRegistrazioneController(credenziali);
    }

    private boolean processaRegistrazioneController(ProfileBean credenziali) {
        if (logInController.researchUser(credenziali) != null) {
            ConsolePrinter.printStatus("Utente già registrato! Scegli un altro username.", true);
            ConsolePrinter.readLine("Premi INVIO per ricaricare la pagina...");
            return false;
        }

        logInController.insert(credenziali);
        ConsolePrinter.printStatus("Registrazione effettuata con successo!", false);
        ConsolePrinter.readLine("\nPremi INVIO per tornare all'area login...");
        return true;
    }
}