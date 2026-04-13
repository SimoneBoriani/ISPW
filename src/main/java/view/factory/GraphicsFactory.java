package view.factory;

import controller.AggiungiAutoController;
import controller.CatalogoController;
import controller.LogInController;
import exceptions.GenericSystemException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class GraphicsFactory {

    private static GraphicsFactory instance = null;

    public abstract CatalogoController createCatalogoController();
    public abstract AggiungiAutoController createAggiungiAutoController();
    public abstract LogInController createLoginController();

    public static synchronized GraphicsFactory getGraphicalSingletonFactory() throws GenericSystemException {
        Properties prop = new Properties();

        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(fis);
            if(instance == null){
                if(prop.getProperty("user_choice").equals("GUI")){
                    instance = new GuiGraphicsFactory();
                }

                if(prop.getProperty("user_choice").equals("CLI")){
                    instance = new CliGraphicsFactory();
                }
            }
        } catch (IOException e) {
            throw new GenericSystemException(e.getMessage());
        }
        return instance;
    }


}
