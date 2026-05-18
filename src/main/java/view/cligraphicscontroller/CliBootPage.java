package view.cligraphicscontroller;

import utils.ConsolePrinter;
import utils.SessionSingleton;

public class CliBootPage {

    private static final String CMD_1 = "1";
    private static final String CMD_2 = "2";
    private static final String CMD_3 = "3";
    private static final String CMD_4 = "4";
    private static final String CMD_EXIT = "0";

    public void render() {
        boolean running = true;
        while (running) {
            ConsolePrinter.printHeader("Krusty No Dusty Rental");

            ConsolePrinter.printMenuOption(CMD_1, "Visualizza Catalogo");

            if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
                ConsolePrinter.printMenuOption(CMD_2, "Accedi al Sistema");
            } else {
                ConsolePrinter.printMenuOption(CMD_2, "Log out");
            }

            ConsolePrinter.printMenuOption(CMD_3, "Gestione Profilo");
            ConsolePrinter.printMenuOption(CMD_4, "Auto noleggiate");
            ConsolePrinter.printMenuOption(CMD_EXIT, "Esci");

            String choice = ConsolePrinter.readLine("Selezione > ").trim();

            switch (choice) {
                case CMD_1 -> navigateToCatalogo();
                case CMD_2 -> {
                    if (SessionSingleton.getInstance().getUtenteCorrente() == null) {
                        navigateToLogin();
                    } else {
                        SessionSingleton.getInstance().setUtenteCorrente(null);
                        ConsolePrinter.printStatus("Log out effettuato con successo!", false);
                        ConsolePrinter.readLine("Premi INVIO per continuare...");
                    }
                }
                case CMD_3 -> navigateToProfile();
                case CMD_4 -> navigateToRented();
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