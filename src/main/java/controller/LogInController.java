package controller;

import bean.LoginBean;
import exeption.IncorrectCredentialEX;
import model.utente.Utente;

public class LogInController {
    public void authenticate(LoginBean loginBean) throws IncorrectCredentialEX {

        int user_id = Integer.parseInt(loginBean.getUsername());
        String username = loginBean.getUsername();
        String password = loginBean.getPassword();
        boolean titolare=loginBean.isTitolare();

        Utente utente = new Utente(user_id,username,password,titolare);

    }
}
