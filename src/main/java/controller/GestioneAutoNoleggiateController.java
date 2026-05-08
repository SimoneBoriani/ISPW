package controller;

import bean.ProfileBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.utente.Utente;

import java.util.List;

public class GestioneAutoNoleggiateController {

    public List<Macchina> findCar(ProfileBean bean){

        Utente utente=new Utente();
        utente.setIdUser(bean.getId());

        return DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getUserCars(utente);
    }

    public void endRent(){
        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().sbloccaAutoScadute("Chiusura Anticipata");
    }
}
