package controller;

import bean.CatalogoBean;
import exceptions.CarNotFoundException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import utils.SessionSingleton;

import java.util.List;

public class VisualizzaCatalogoController {

    public List<Macchina> getCars() {
        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().sbloccaAutoScadute("Chiusura Naturale");
        return DaoFactory.getDaoSingletonFactory().createMacchinaDao().getCars();
    }

    public List<Macchina> research(CatalogoBean filtri) throws CarNotFoundException {

        Macchina autoFiltro = new Macchina();

        autoFiltro.setId(filtri.getId());
        autoFiltro.setMarca(filtri.getMarca());
        autoFiltro.setModello(filtri.getModello());
        autoFiltro.setAlimentazione(filtri.getAlimentazione());
        autoFiltro.setTrasmissione(filtri.getTrasmissione());
        autoFiltro.setPrezzo(filtri.getPrezzo());

        return DaoFactory.getDaoSingletonFactory().createMacchinaDao().research(autoFiltro);
    }

    public Macchina getAutoSelezionataDaSessione() {
        return SessionSingleton.getInstance().getAutoSelezionata();
    }

    public void pulisciSelezioneSessione() {
        SessionSingleton.getInstance().setAutoSelezionata(null);
    }

}