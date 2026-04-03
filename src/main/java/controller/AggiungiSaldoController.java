package controller;

import app.SessionSingleton;
import bean.AggiungiSaldoBean;
import bean.LoginBean;
import model.utente.Utente;

public class AggiungiSaldoController {


    public void AggiungiSaldo(double saldo){

        Utente utenteCorrente = SessionSingleton.getInstance().getUtenteCorrente();
        LoginBean utenteLoggato = new LoginBean(utenteCorrente.getUsername(), utenteCorrente.getUserPassword());
        utenteLoggato.setSaldo(utenteCorrente.getSaldo()+saldo);
        AggiungiSaldoBean aggiungiSaldoBean = new AggiungiSaldoBean();
        aggiungiSaldoBean.sendInfo(utenteLoggato);

    }
}
