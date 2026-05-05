package controller;

import bean.ProfileBean;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.utente.Utente;

import java.util.List;

public class GestioneProfiloController {

    public List<Macchina> getUserCar(ProfileBean profileBean){

        Utente utente=new Utente();

        utente.setIdUser(profileBean.getId());

        return DaoFactory.getDaoSingletonFactory().createAcquistoAutoDao().getUserCars(utente);

    }

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
