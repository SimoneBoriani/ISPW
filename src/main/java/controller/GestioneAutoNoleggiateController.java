package controller;

import bean.ProfileBean;
import bean.NotificaBean;
import model.daofactory.DaoFactory;
import model.utente.Utente;
import model.noleggioauto.NoleggioAuto;
import view.factory.ControllerFactory;

import java.util.List;

public class GestioneAutoNoleggiateController {

    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();


    public List<NoleggioAuto> findRentals(ProfileBean bean){

        Utente utente = new Utente();
        utente.setIdUser(bean.getId());

        return DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getUserCars(utente);
    }

    public void endRent(int id){
        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().terminaNoleggio(id,"Chiusura Anticipata");
    }

    public void segnalazione(NotificaBean bean){
        if(bean!=null){
         //   notificheController.inviaMessaggioAdAdmin(String.valueOf(bean.getUtente().getIdUser()),bean.getUtente().getNome());
        } else throw new NullPointerException("Segnalazione non trovata");
    }

}