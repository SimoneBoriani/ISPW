package controller;

import app.SessionSingleton;
import bean.LoginBean;
import model.utente.Utente;
import model.utente.dao.DbmsDaoUtente;

import java.sql.SQLException;
import java.util.logging.Logger;

public class LogInController {

    Logger logger = Logger.getLogger(getClass().getName());

    public void authenticate(LoginBean loginBean) {

        if(loginBean.sendInfoAuth()){
            SessionSingleton ss = SessionSingleton.getInstance();
            ss.setUtenteCorrente(researchUser(loginBean));
        }
    }

    public Utente researchUser(LoginBean loginBean) {
        try {
            return DbmsDaoUtente.researchUser(loginBean);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(LoginBean loginBean){
        loginBean.sendInfoInsert();
    }
}
