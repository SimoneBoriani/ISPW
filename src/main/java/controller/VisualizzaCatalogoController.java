package controller;

import bean.CatalogoBean;
import exceptions.CarNotFoundException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SessionSingleton;

import java.util.List;

public class VisualizzaCatalogoController {

    private final Logger log = LogManager.getLogger(VisualizzaCatalogoController.class);

    public void eseguiSbloccoAsincrono() {
        Thread sbloccoThread = new Thread(() -> {
            try {
                DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().sbloccaAutoScadute();
            } catch (Exception e) {
                log.error("Errore durante lo sblocco automatico: {} ",e.getMessage());
            }
        });
        sbloccoThread.setDaemon(true);
        sbloccoThread.start();
    }

    public List<Macchina> getCars() {
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
        autoFiltro.setTipologia(filtri.getTipologia());

        return DaoFactory.getDaoSingletonFactory().createMacchinaDao().research(autoFiltro);
    }

    public void setSessione(Macchina macchina){
        SessionSingleton.getInstance().setAutoSelezionata(macchina);
    }
}