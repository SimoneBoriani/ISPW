package start;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Selection {

    private static final Logger LOGGER = LogManager.getLogger(Selection.class);
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";
    private static final String CHOICE_PROP_KEY = "user_choice";

    private static final Integer DEBUG_CHOICE = 1;

    public void init() {
        int choice = getChoice();
        String mode = (choice == 2) ? "CLI" : "GUI";

        updateConfiguration(mode);

        if ("GUI".equals(mode)) {
            Launcher.starter();
        } else {
            LOGGER.info("Modalità CLI avviata correttamente.");
        }
    }

    private int getChoice() {

        if (DEBUG_CHOICE != null) {
            LOGGER.info("[DEBUG] Utilizzo scelta forzata: {}", DEBUG_CHOICE);
            return DEBUG_CHOICE;
        }

        LOGGER.info("SCEGLI LA MODALITA' GRAFICA:");
        LOGGER.info("1. GUI");
        LOGGER.info("2. CLI");

        Scanner scanner = new Scanner(System.in);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            LOGGER.warn("Input non valido, utilizzo default: GUI");
            return 1;
        }
    }

    private void updateConfiguration(String mode) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            prop.load(input);
        } catch (IOException e) {
            LOGGER.warn("Impossibile leggere il file, ne verrà creato uno nuovo: {}", e.getMessage());
        }

        prop.setProperty(CHOICE_PROP_KEY, mode);

        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            prop.store(output, "Aggiornamento modalità interfaccia");
        } catch (IOException e) {
            LOGGER.error("Errore critico nel salvataggio della configurazione", e);
            throw new UncheckedIOException(e);
        }
    }
}