package view.factory;

import controller.AggiungiAutoController;
import controller.MainPageCatalogoController;
import controller.LogInController;
import view.guigraphicscontroller.GuiMainPageCatalogoController;

public  class GuiGraphicsFactory extends GraphicsFactory{

    public GuiGraphicsFactory(){

    }

    @Override
    public MainPageCatalogoController createCatalogoController() {
        return new GuiMainPageCatalogoController();
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