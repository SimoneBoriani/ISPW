package view.cligraphicscontroller;

import utils.ConsolePrinter;
import utils.SessionSingleton;

public class CliBootPage {

    private static final String CMD_CATALOGO = "1";
    private static final String CMD_ACCESSO = "2";
    private static final String CMD_EXIT = "0";
    private static final String CMD_BACK="Premi INVIO per tornare...";

    public void render() {
        boolean running = true;
        while (running) {
            ConsolePrinter.printHeader("Krusty No Dusty Rental");

            ConsolePrinter.printMenuOption(CMD_CATALOGO, "Visualizza Catalogo");

            if(SessionSingleton.getInstance().getUtenteCorrente()==null){ConsolePrinter.printMenuOption(CMD_ACCESSO, "Accedi al Sistema");}
            else{ConsolePrinter.printMenuOption(CMD_ACCESSO,"Log out");}

            ConsolePrinter.printMenuOption(CMD_EXIT, "Esci");

            String choice = ConsolePrinter.readLine("Selezione > ").trim();

            switch (choice) {
                case CMD_CATALOGO -> navigateToCatalogo();
                case CMD_ACCESSO -> navigateToLogin();
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
        ConsolePrinter.printStatus("Modulo Catalogo non ancora implementato.", false);
        ConsolePrinter.readLine(CMD_BACK);
    }

    private void navigateToLogin() {
        new CliLogInController().render();
    }

}