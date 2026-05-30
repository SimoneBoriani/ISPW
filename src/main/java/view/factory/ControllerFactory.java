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

    public GestioneCatalogoController createGestioneCatalogoController(){
        return new GestioneCatalogoController();
    }

    public VisualizzaCatalogoController createVisualizzaCatalogoController(){
        return new VisualizzaCatalogoController();
    }

    public NoleggioController createNoleggioController(){return new  NoleggioController();}

    public GestioneProfiloController createGestioneProfiloController(){return new GestioneProfiloController();}

    public GestioneAutoNoleggiateController createGestioneAutoNoleggiateController(){return new GestioneAutoNoleggiateController();}

    public VisualizzaStoricoController createVisualizzaStoricoController(){return new VisualizzaStoricoController();}

    public NotificheController createNotificheController(){return new NotificheController();}

    public  RicaricaController createRicaricaController(){return new RicaricaController();}

    public static synchronized ControllerFactory getGraphicalSingletonFactory() throws GenericSystemException {

        if (instance == null) {
                instance = new  ControllerFactory();
            }
        return instance;
        }
}
