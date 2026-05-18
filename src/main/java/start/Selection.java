package start;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConsolePrinter;
import view.cligraphicscontroller.CliBootPage;

import java.io.*;
import java.util.Properties;

public class Selection {

    private static final Logger LOGGER = LogManager.getLogger(Selection.class);

    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";
    private static final String CHOICE_PROP_KEY = "user_choice";
    private static final String PERSISTENCE_PROP_KEY = "persistence";

    private static final String CMD_1 = "1";
    private static final String CMD_2 = "2";
    private static final String CMD_3 = "3";

    public void init() {

        int choice = getChoice();
        String mode = (choice == 2) ? "CLI" : "GUI";

        String confPers = getPersistence();

        updateConfiguration(mode, confPers);

        if ("GUI".equals(mode)) {
            Launcher.starter();
        } else {
            LOGGER.info("Modalità CLI avviata correttamente.");
            new CliBootPage().render();
            LOGGER.info("Modalità CLI terminata correttamente.");
        }
    }

    private int getChoice() {

        ConsolePrinter.printHeader("SCEGLI LA MODALITA' GRAFICA");
        ConsolePrinter.printMenuOption(CMD_1, "GUI");
        ConsolePrinter.printMenuOption(CMD_2, "CLI");

        String choice = ConsolePrinter.readLine("Selezione > ").trim();

        switch (choice) {
            case CMD_1 -> { return 1; }
            case CMD_2 -> { return 2; }
            default    -> { return 1; }
        }
    }

    private String getPersistence() {
        String str;
        ConsolePrinter.printHeader("SCEGLI LA PERSISTENZA");

        ConsolePrinter.printMenuOption(CMD_1, "JDBC");
        ConsolePrinter.printMenuOption(CMD_2, "FILE");
        ConsolePrinter.printMenuOption(CMD_3, "DEMO");

        String choice = ConsolePrinter.readLine("Selezione > ").trim();

        switch (choice) {
            case CMD_1 -> str = "JDBC";
            case CMD_2 -> str = "FILE";
            case CMD_3 -> str = "DEMO";
            default    -> str = "DEMO";
        }
        return str;
    }

    private void updateConfiguration(String mode, String persistence) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            prop.load(input);
        } catch (IOException e) {
            LOGGER.warn("Impossibile leggere il file sorgente originale: {}", e.getMessage());
        }

        prop.setProperty(CHOICE_PROP_KEY, mode);
        prop.setProperty(PERSISTENCE_PROP_KEY, persistence);

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            prop.store(output, "Aggiornamento sorgenti");
            output.flush();
        } catch (IOException e) {
            LOGGER.error("Errore nel salvataggio del file sorgente", e);
        }
    }
}