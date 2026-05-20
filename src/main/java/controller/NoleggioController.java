package controller;

import bean.NoleggioAutoBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.utente.Utente;
import model.duratacontrattuale.*;

public class NoleggioController {

    public double calcolaTotale(Macchina auto, int giorni) {

        PianoNoleggio pianoScelto = determinaPiano(giorni);
        return pianoScelto.calcolaPrezzo(auto.getPrezzo(), giorni);

    }

    public void processaNoleggio(NoleggioAutoBean bean) {

        Macchina auto = bean.getMacchina();
        Utente utente = bean.getRenter();
        int giorni=bean.getGiorni();

        if(giorni<0){
            throw new IllegalArgumentException("Giorni non devono essere negativi.");
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
}