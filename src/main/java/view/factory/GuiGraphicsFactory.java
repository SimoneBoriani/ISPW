package view.factory;

import controller.AggiungiAutoController;
import controller.CatalogoController;
import controller.LogInController;
import view.sbcontroller.GuiPageManager;


public  class GuiGraphicsFactory extends GraphicsFactory{

    public GuiGraphicsFactory(){

    }
    public GuiPageManager crateGuiPageManager(){
        return new GuiPageManager();
    }

    @Override
    public CatalogoController createCatalogoController() {
        return new CatalogoController();
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