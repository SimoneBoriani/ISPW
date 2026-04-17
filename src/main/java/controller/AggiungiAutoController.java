package controller;

import bean.AggiungiAutoBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

public class AggiungiAutoController {

    public void addCar(AggiungiAutoBean aggiungiAutoBean){

        Macchina toAddCar = new Macchina(
                aggiungiAutoBean.getCarYear(),
                aggiungiAutoBean.getCarKm(),
                aggiungiAutoBean.getCarSeat(),
                aggiungiAutoBean.getCarOwners(),
                aggiungiAutoBean.getCarName(),
                aggiungiAutoBean.getCarBrand(),
                aggiungiAutoBean.getCarAlimentation(),
                aggiungiAutoBean.getCarPrice(),
                aggiungiAutoBean.getCarType()
        );

        DaoFactory.getDaoSingletonFactory().createMacchinaDao().insert(toAddCar);

    }
}
