package utils;

import exceptions.GenericSystemException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE_PATH = "src/main/resources/secrets.properties";

    static {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            throw new GenericSystemException("Impossibile caricare il file di configurazione: " + CONFIG_FILE_PATH, e);
        }
    }

    private ConfigLoader(){
        //Previene l'istanz.
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Chiave non trovata nel file di configurazione: " + key);
        }
        return value;
    }

    public static int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il valore per la chiave '" + key + "' non è un numero intero valido.", e);
        }
    }

    public static ZoneId getTimeZone() {
        try {
            String zoneString = properties.getProperty("app.timezone", "Europe/Rome");
            return ZoneId.of(zoneString);
        } catch (Exception e) {
            return ZoneId.systemDefault();
        }
    }
}