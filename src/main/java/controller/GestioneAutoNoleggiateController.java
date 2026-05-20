package controller;

import bean.ProfileBean;
import model.daofactory.DaoFactory;
import model.utente.Utente;
import model.noleggioauto.NoleggioAuto;

import java.util.List;

public class GestioneAutoNoleggiateController {

    public List<NoleggioAuto> findRentals(ProfileBean bean){

        Utente utente = new Utente();
        utente.setIdUser(bean.getId());

        return DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getUserCars(utente);
    }

    public void endRent(int id){
        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().terminaNoleggio(id,"Chiusura Anticipata");
    }
}