package controller;

import bean.CatalogoBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GestioneCatalogoController {

    private final Logger logger = Logger.getLogger(String.valueOf(GestioneCatalogoController.class));
    private final List<Macchina> autoDaAggiungereAlDB = new ArrayList<>();
    private final VisualizzaCatalogoController visualizzaCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createVisualizzaCatalogoController();

    public List<Macchina> getCars() {
        return visualizzaCatalogoController.getCars();
    }

    public void removeCar(CatalogoBean bean) {
        DaoFactory.getDaoSingletonFactory().createMacchinaDao().remove(bean.getId());
    }


    public void validaEModificaAuto(CatalogoBean bean) throws IllegalArgumentException {
        validaBean(bean);
        modifyCar(bean);
    }


    public void validaEAggiungiAuto(CatalogoBean bean) throws IllegalArgumentException {
        validaBean(bean);
        salvaAutoRam(bean);
    }


    private void validaBean(CatalogoBean bean) throws IllegalArgumentException {
        if (bean == null) {
            throw new IllegalArgumentException("Dati auto non validi.");
        }
        if (bean.getMarca() == null || bean.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo Marca è obbligatorio.");
        }
        if (bean.getModello() == null || bean.getModello().trim().isEmpty()) {
            throw new IllegalArgumentException("Il campo Modello è obbligatorio.");
        }
        if (bean.getPrezzo() <= 0) {
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di zero.");
        }
        if (bean.getAnno() <= 1900) {
            throw new IllegalArgumentException("Inserisci un anno valido.");
        }
        if (bean.getPosti() <= 0) {
            throw new IllegalArgumentException("Il numero di posti non è valido.");
        }
        if (bean.getFoto() == null || bean.getFoto().trim().isEmpty()) {
            bean.setFoto("no_image.png");
        }
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

    public void salvaAutoRam(CatalogoBean bean) {
        Macchina nuovaAuto = new Macchina();
        nuovaAuto.setMarca(bean.getMarca());
        nuovaAuto.setModello(bean.getModello());
        nuovaAuto.setAnno(bean.getAnno());
        nuovaAuto.setPosti(bean.getPosti());
        nuovaAuto.setAlimentazione(bean.getAlimentazione());
        nuovaAuto.setTrasmissione(bean.getTrasmissione());
        nuovaAuto.setPrezzo(bean.getPrezzo());
        nuovaAuto.setTipologia(bean.getTipologia());
        nuovaAuto.setImageUrl(bean.getFoto());
        nuovaAuto.setDisponibile(true);

        autoDaAggiungereAlDB.add(nuovaAuto);
    }

    public void confermaSalvataggio() {
        if (autoDaAggiungereAlDB.isEmpty()) {
            logger.info("Nessuna nuova auto da inserire nel db");
        } else {
            DaoFactory.getDaoSingletonFactory().createMacchinaDao().insert(autoDaAggiungereAlDB);
            logger.info("Auto aggiunte al DB con successo!");
            autoDaAggiungereAlDB.clear();
        }
    }

    public Macchina createAutoSegnalata() {

        CatalogoBean bean = new CatalogoBean();
        bean.setId(Integer.parseInt(SessionSingleton.getInstance().getTempIdNotifica()));

        if (bean.getId() != 0) {
            bean.setMarca(SessionSingleton.getInstance().getTempMarca());
            bean.setModello(SessionSingleton.getInstance().getTempModello());

            List<Macchina> list = visualizzaCatalogoController.research(bean);
            return list.get(0);
        } else return null;
    }
}