package model.acquistoauto.dao;

import model.macchina.Macchina;
import model.utente.Utente;

public abstract class DaoAcquistoAuto {

    public abstract boolean checkInfo(Utente utente,Macchina macchina);

}
