package controller;

import bean.ProfileBean;
import model.daofactory.DaoFactory;
import model.utente.Utente;


public class GestioneProfiloController {

    public void updateProfile(ProfileBean profileBean){

        Utente utente=new Utente();

        utente.setIdUser(profileBean.getId());
        utente.setNome(profileBean.getNome());
        utente.setCognome(profileBean.getCognome());
        utente.setUsername(profileBean.getUsername());
        utente.setUserPassword(profileBean.getPassword());
        utente.setSaldo(profileBean.getSaldo());

        DaoFactory.getDaoSingletonFactory().createUtenteDao().update(utente);

    }

}
