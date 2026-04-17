package controller;

import utils.SessionSingleton;
import bean.LoginBean;
import exceptions.GenericSystemException;
import exceptions.IncorrectCredentialExeption;
import model.daofactory.DaoFactory;
import model.utente.Utente;

import java.sql.SQLException;


public class LogInController {

    public void authenticate(LoginBean loginBean){

        Utente utenteDati = new Utente(-1, loginBean.getUsername(), loginBean.getPassword(), null, null);

        boolean isAuthenticated = DaoFactory.getDaoSingletonFactory().createUtenteDao().authenticateUser(utenteDati);

        if (isAuthenticated) {

            Utente utenteCompleto = null;

            try {

                utenteCompleto = DaoFactory.getDaoSingletonFactory().createUtenteDao().researchUser(utenteDati);

            } catch (SQLException e) {

                throw new GenericSystemException(e);

            }

            SessionSingleton.getInstance().setUtenteCorrente(utenteCompleto);

        } else {
            throw new IncorrectCredentialExeption("Username o Password errati.");
        }
    }

    public boolean researchUser(LoginBean loginBean) {

        Utente ricercato = new Utente(-1, loginBean.getUsername(), "", "", "");

        try {

            Utente utenteEsistente=DaoFactory.getDaoSingletonFactory().createUtenteDao().researchUser(ricercato);

            return utenteEsistente != null;

        } catch (SQLException e) {

            throw new GenericSystemException(e);

        }
    }

    public void insert(LoginBean loginBean){

        Utente nuovo=new Utente(-1, loginBean.getUsername(), loginBean.getPassword(), null, null);
        DaoFactory.getDaoSingletonFactory().createUtenteDao().insertUtente(nuovo);

    }
}
