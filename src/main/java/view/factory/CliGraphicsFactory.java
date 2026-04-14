package view.factory;

import controller.AggiungiAutoController;
import controller.MainPageCatalogoController;
import controller.LogInController;

public class CliGraphicsFactory extends GraphicsFactory{

    @Override
    public MainPageCatalogoController createCatalogoController() {
        return new MainPageCatalogoController();
    }

    @Override
    public AggiungiAutoController createAggiungiAutoController() {
        return new AggiungiAutoController();
    }

    @Override
    public LogInController createLoginController() {
        return new  LogInController();
    }
}
