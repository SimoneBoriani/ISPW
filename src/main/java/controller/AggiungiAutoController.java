package controller;

import bean.AggiungiAutoBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

public class AggiungiAutoController {

    public void addCar(AggiungiAutoBean aggiungiAutoBean){

        int carYear = aggiungiAutoBean.getCarYear();
        String carModel =aggiungiAutoBean.getCarName();
        int carPrice =aggiungiAutoBean.getCarPrice();
        String carAlimentation =aggiungiAutoBean.getCarAlimentation();
        String carBrand =aggiungiAutoBean.getCarBrand();
        int carSeat =aggiungiAutoBean.getCarSeat();
        int carOwners =aggiungiAutoBean.getCarOwners();
        int carKm =aggiungiAutoBean.getCarKm();
        String carType =aggiungiAutoBean.getCarType();

        Macchina toAddCar = new Macchina(carYear, carKm, carSeat, carOwners, carModel, carBrand, carAlimentation, carPrice, carType);

        DaoFactory.getDaoSingletonFactory().createMacchinaDao().insert(toAddCar);
    }
}
