package controller;

import bean.CatalogoBean;
import exceptions.CarNotFoundException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import java.util.List;

public class MainPageCatalogoController {

    public List<Macchina> getCars() {
        return DaoFactory.getDaoSingletonFactory().createMacchinaDao().getCars();
    }

    public List<Macchina> research(CatalogoBean filtri) throws CarNotFoundException {

        String brand= filtri.getMarca();
        String model= filtri.getModello();
        String alimentation= filtri.getAlimentazione();
        int kmMax=filtri.getKm();

        return DaoFactory.getDaoSingletonFactory().createMacchinaDao().research(brand,model,alimentation,kmMax);
    }
}