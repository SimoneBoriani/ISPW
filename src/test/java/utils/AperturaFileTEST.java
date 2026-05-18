package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;



public class AperturaFileTEST {

    private static Logger log = LogManager.getLogger(AperturaFileTEST.class);

    public static void open(){

        Properties prop = new Properties();
        String path = "src/main/resources/config.properties";

        try (FileInputStream configInput = new FileInputStream(path)) {
            prop.load(configInput);
        } catch (Exception e) {
            log.error("Impossibile leggere il file, verrà creato da zero:{}",e.getMessage());
        }

        prop.setProperty("persistence", "DEMO");

        try (FileOutputStream configOutput = new FileOutputStream(path)) {
            prop.store(configOutput, "Forzato avvio in modalità DEMO TEST");
        } catch (Exception e) {
            log.error("Errore durante la scrittura sul file:{}",e.getMessage());
        }
    }
}
