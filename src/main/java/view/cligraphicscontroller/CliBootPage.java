package view.cligraphicscontroller;

import utils.ConsolePrinter;
import utils.SessionSingleton;

public class CliBootPage {

    private static final String CMD_CATALOGO = "1";
    private static final String CMD_ACCESSO = "2";
    private static final String CMD_PROFILO = "3";
    private static final String CMD_AUTONOLEGGIATE = "4";
    private static final String CMD_EXIT = "0";

    public void render() {
        boolean running = true;
        while (running) {
            ConsolePrinter.printHeader("Krusty No Dusty Rental");

            ConsolePrinter.printMenuOption(CMD_CATALOGO, "Visualizza Catalogo");

            if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
                ConsolePrinter.printMenuOption(CMD_ACCESSO, "Accedi al Sistema");
            } else {
                ConsolePrinter.printMenuOption(CMD_ACCESSO, "Log out");
            }

            ConsolePrinter.printMenuOption(CMD_PROFILO, "Gestione Profilo");
            ConsolePrinter.printMenuOption(CMD_AUTONOLEGGIATE, "Auto noleggiate");
            ConsolePrinter.printMenuOption(CMD_EXIT, "Esci");

            String choice = ConsolePrinter.readLine("Selezione > ").trim();

            switch (choice) {
                case CMD_CATALOGO -> navigateToCatalogo();
                case CMD_ACCESSO -> {
                    if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
                        navigateToLogin();
                    } else {
                        SessionSingleton.getInstance().setUtenteCorrente(null);
                        ConsolePrinter.printStatus("Log out effettuato con successo!", false);
                        ConsolePrinter.readLine("Premi INVIO per continuare...");
                    }
                }
                case CMD_PROFILO -> navigateToProfile();
                case CMD_AUTONOLEGGIATE -> navigateToRented();
                case CMD_EXIT -> {
                    ConsolePrinter.printStatus("Arrivederci!", false);
                    running = false;
                }
                default -> {
                    ConsolePrinter.printStatus("Scelta non valida!", true);
                    ConsolePrinter.readLine("Premi INVIO per riprovare...");
                }
            }
        }
    }

    private void navigateToCatalogo() {
        new CliVisualizzaCatalogoPage().render();
    }

    private void navigateToLogin() {
        new CliLogInPage().render();
    }

    private void navigateToProfile() {
        new CliProfilePage().render();
    }

    private void navigateToRented() {
        new CliAutoNoleggiatePage().render();
    }
}