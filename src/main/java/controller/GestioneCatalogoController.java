package controller;

import bean.CatalogoBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

public class GestioneCatalogoController {

    public void removeCar(CatalogoBean catalogo){
        DaoFactory.getDaoSingletonFactory().createMacchinaDao().remove(catalogo.getId());
    }

    public void applyDiscount(CatalogoBean catalogo){
        //TESTING
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
}
