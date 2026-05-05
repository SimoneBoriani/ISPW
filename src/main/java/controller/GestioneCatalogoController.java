package controller;

import bean.AcquistoAutoBean;
import bean.AggiungiAutoBean;
import bean.CatalogoBean;
import exceptions.GenericSystemException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

public class GestioneCatalogoController {

    public void removeCar(CatalogoBean bean){
        DaoFactory.getDaoSingletonFactory().createMacchinaDao().remove(bean.getId());
    }

    public void addCar(AggiungiAutoBean aggiungiAutoBean){ //catalogo bean

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

    public void modifyCar(CatalogoBean catalogo) {

        Macchina macchinaSelezionata = new Macchina();

        macchinaSelezionata.setId(catalogo.getId());
        macchinaSelezionata.setPrezzo(catalogo.getPrezzo());
        macchinaSelezionata.setModello(catalogo.getModello());
        macchinaSelezionata.setAlimentazione(catalogo.getAlimentazione());
        macchinaSelezionata.setCasa(catalogo.getMarca());
        macchinaSelezionata.setModello(catalogo.getModello());
        macchinaSelezionata.setAnno(catalogo.getAnno());
        macchinaSelezionata.setImageUrl(catalogo.getFoto());
        macchinaSelezionata.setKm(catalogo.getKm());
        macchinaSelezionata.setPosti(catalogo.getPosti());
        macchinaSelezionata.setTipologia(catalogo.getTipologia());

        DaoFactory.getDaoSingletonFactory().createMacchinaDao().update(macchinaSelezionata);
    }

    public void buyRequest(AcquistoAutoBean bean){
        if(checkData(bean)){
            DaoFactory.getDaoSingletonFactory().createAcquistoAutoDao().buyRequest(bean.getBuyer(),bean.getMacchina());
        }
        else{
            throw new GenericSystemException("Auto / Utente non trovato");
        }
    }

    public boolean checkData(AcquistoAutoBean bean){

        return DaoFactory.getDaoSingletonFactory().createAcquistoAutoDao().checkInfo(bean.getBuyer(),bean.getMacchina());

    }
}
