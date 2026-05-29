package controller;

import bean.NoleggioAutoBean;
import bean.NotificaBean;
import exceptions.DocsNotValidException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.utente.Utente;
import model.duratacontrattuale.*;
import view.factory.ControllerFactory;

public class NoleggioController {

    private final VisualizzaCatalogoController controllerApplicativo= ControllerFactory.getGraphicalSingletonFactory().createVisualizzaCatalogoController();
    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();


    public double calcolaTotale(Macchina auto, int giorni) {

        PianoNoleggio pianoScelto = determinaPiano(giorni);
        return pianoScelto.calcolaPrezzo(auto.getPrezzo(), giorni);

    }

    public Macchina getAuto(){
        return controllerApplicativo.getAutoSelezionataDaSessione();
    }

    public void clear(){
        controllerApplicativo.pulisciSelezioneSessione();
    }

    public void processaNoleggio(NoleggioAutoBean bean) {

        Macchina auto = bean.getMacchina();
        Utente utente = bean.getRenter();
        int giorni=bean.getGiorni();

        if(giorni<0){
            throw new IllegalArgumentException("Giorni non devono essere negativi.");
        }

        if (Boolean.FALSE.equals(utente.getVerificato()) || utente.getVerificato() == null) {
            throw new DocsNotValidException("Impossibile noleggiare: Patente non verificata.");
        }

        double totale = calcolaTotale(auto, giorni);

        if (utente.getSaldo() < totale) {
            throw new IllegalArgumentException("Saldo insufficiente");
        }

        bean.getMacchina().setPrezzo(totale);

        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().rentRequest(utente, auto,giorni);

    }

    public PianoNoleggio determinaPiano(int giorni) {

        if (giorni <= 0) {
            throw new IllegalArgumentException("Il numero di giorni deve essere maggiore di zero.");
        }

        if (giorni <= 30) {
            return new NoleggioBreveTermine();
        }
        else if (giorni <= 364) {
            return new NoleggioMedioTermine();
        }
        else if (giorni <= 700) {
            return new NoleggioLungoTermine();
        }
        else {
            throw new IllegalArgumentException("Il limite massimo di noleggio è 700 giorni.");
        }
    }

    public void segnalazione(NotificaBean bean){
        if(bean!=null){
            notificheController.inviaMessaggioAdAdmin(bean);
        } else throw new NullPointerException("Segnalazione non trovata");
    }

    public void notificaSistema(NotificaBean bean){
        if(bean!=null){
            notificheController.generaNotificaSistema(bean);
        } else throw new NullPointerException("Segnalazione non trovata");
    }
}