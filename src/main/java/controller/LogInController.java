package controller;

import bean.LoginBean;
import model.utente.Utente;
import model.utente.dao.DaoUtente;

import java.util.logging.Logger;

public class LogInController {

    Logger logger = Logger.getLogger(getClass().getName());

    public Utente researchUser(LoginBean loginBean) {
        return DaoUtente.
    }

    public void insert(LoginBean loginBean){
        loginBean.sendInfoInsert();
    }
}
