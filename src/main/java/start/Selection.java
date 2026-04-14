package start;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class Selection {

    private static final Logger logger = LogManager.getLogger(Selection.class.getName());
    public Selection(){}

    public void init(){

        String configFilePath = "src/main/resources/config.properties";
        int choice;
        Properties prop = new Properties();

        logger.info("SCEGLI LA MODALITA' GRAFICA:");
        logger.info("1. GUI");
        logger.info("2. CLI");

        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try (FileInputStream configFile = new FileInputStream(configFilePath)) {
            prop.load(configFile);
            String choiceProperty = "user_choice";
            choice =1; //Integer.parseInt(reader.readLine());

            switch(choice){

                case 1:
                    prop.setProperty(choiceProperty, "GUI");
                    prop.store(new FileOutputStream(configFilePath), "case gui");
                    Launcher.starter();
                    break;

                case 2:
                    prop.setProperty(choiceProperty, "CLI");
                    prop.store(new FileOutputStream(configFilePath), "case cli");
                    break;

                default:
                    prop.setProperty(choiceProperty, "GUI");
                    prop.store(new FileOutputStream(configFilePath), "default(gui)");
                    Launcher.starter();
                    break;
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}