package controller;

import bean.CatalogoBean;
import bean.NoleggioAutoBean;
import exceptions.GenericSystemException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GestioneCatalogoController {

    private final Logger logger = Logger.getLogger(String.valueOf(GestioneCatalogoController.class));
    private final List<Macchina> autoDaAggiungereAlDB = new ArrayList<>();

    public void removeCar(CatalogoBean bean){
        DaoFactory.getDaoSingletonFactory().createMacchinaDao().remove(bean.getId());
    }

    public void modifyCar(CatalogoBean catalogo) {

        Macchina macchinaSelezionata = new Macchina();

        macchinaSelezionata.setId(catalogo.getId());
        macchinaSelezionata.setPrezzo(catalogo.getPrezzo());
        macchinaSelezionata.setModello(catalogo.getModello());
        macchinaSelezionata.setMarca(catalogo.getMarca());
        macchinaSelezionata.setAlimentazione(catalogo.getAlimentazione());
        macchinaSelezionata.setTrasmissione(catalogo.getTrasmissione());
        macchinaSelezionata.setAnno(catalogo.getAnno());
        macchinaSelezionata.setImageUrl(catalogo.getFoto());
        macchinaSelezionata.setPosti(catalogo.getPosti());
        macchinaSelezionata.setTipologia(catalogo.getTipologia());

        DaoFactory.getDaoSingletonFactory().createMacchinaDao().update(macchinaSelezionata);
    }

    public void rentRequest(NoleggioAutoBean bean){
        if(checkData(bean)){
            DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().rentRequest(bean.getRenter(), bean.getMacchina(),bean.getGiorni());
        }
        else{
            throw new GenericSystemException("Saldo insufficiente.");
        }
    }

    public boolean checkData(NoleggioAutoBean bean){
        return DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().checkInfo(bean.getRenter(), bean.getMacchina());
    }

    public void salvaAutoRam(CatalogoBean bean){

        Macchina nuovaAuto = new Macchina();

        nuovaAuto.setMarca(bean.getMarca());
        nuovaAuto.setModello(bean.getModello());
        nuovaAuto.setAnno(bean.getAnno());
        nuovaAuto.setPosti(bean.getPosti());
        nuovaAuto.setAlimentazione(bean.getAlimentazione());
        nuovaAuto.setTrasmissione(bean.getTrasmissione());
        nuovaAuto.setPrezzo(bean.getPrezzo());
        nuovaAuto.setTipologia(bean.getTipologia());
        nuovaAuto.setImageUrl("no_image.png");

        autoDaAggiungereAlDB.add(nuovaAuto);

    }

    public void confermaSalvataggio() {

        if (autoDaAggiungereAlDB.isEmpty()) {
           logger.info("Nessuna nuova auto da inserire nel db");
            return;
        }

        DaoFactory.getDaoSingletonFactory().createMacchinaDao().insert(autoDaAggiungereAlDB);
        logger.info("Auto aggiunte al DB con successo!");
        autoDaAggiungereAlDB.clear();

    }
}