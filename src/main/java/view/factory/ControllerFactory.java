package view.factory;

import controller.*;
import exceptions.GenericSystemException;

public class ControllerFactory {

    private static ControllerFactory instance = null;

    protected ControllerFactory() {
        //Costruttore
    }

    public LogInController createLoginController(){
        return new LogInController();
    }

    public AggiungiAutoController createAggiungiAutoController(){
        return new AggiungiAutoController();
    }

    public GestioneCatalogoController createGestioneCatalogoController(){
        return new GestioneCatalogoController();
    }

    public MainPageCatalogoController createMainPageCatalogoController(){
        return new MainPageCatalogoController();
    }

    public GestioneProfiloController createGestioneProfiloController(){return new GestioneProfiloController();}

    public static synchronized ControllerFactory getGraphicalSingletonFactory() throws GenericSystemException {

        if (instance == null) {
                instance = new  ControllerFactory();
            }
        return instance;
        }
}
