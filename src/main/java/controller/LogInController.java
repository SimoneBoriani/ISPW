package controller;

import utils.SessionSingleton;
import bean.ProfileBean;
import exceptions.GenericSystemException;
import exceptions.IncorrectCredentialExeption;
import model.daofactory.DaoFactory;
import model.utente.Utente;
import view.factory.ControllerFactory;

import java.sql.SQLException;


public class LogInController {

    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public void authenticate(ProfileBean loginBean){

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

    public Utente researchUser(ProfileBean loginBean) {

        Utente ricercato = new Utente(-1, loginBean.getUsername(), "", "", "");

        try {

            return DaoFactory.getDaoSingletonFactory().createUtenteDao().researchUser(ricercato);

        } catch (SQLException e) {

            throw new GenericSystemException(e);

        }
    }

    public void insert(ProfileBean loginBean) {


        if (loginBean.getUsername() == null || loginBean.getUsername().trim().isEmpty()) {
            throw new IncorrectCredentialExeption("L'username non può essere nullo o vuoto.");
        }

        if (loginBean.getPassword() == null || loginBean.getPassword().trim().isEmpty()) {
            throw new IncorrectCredentialExeption("La password non può essere nulla o vuota.");
        }

        Utente utenteEsistente = researchUser(loginBean);
        if (utenteEsistente != null) {
            throw new IncorrectCredentialExeption("Username già registrato.");
        }

        Utente nuovo = new Utente(-1, loginBean.getUsername(), loginBean.getPassword(), null, null);
        DaoFactory.getDaoSingletonFactory().createUtenteDao().insertUtente(nuovo);
        utenteEsistente = researchUser(loginBean);
        notificheController.generaNotificaSistema(String.valueOf(utenteEsistente.getIdUser()),"Benvenuto in Boro Rental !");
    }
}
