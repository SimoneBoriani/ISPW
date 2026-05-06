package model.noleggioauto.dao;

import model.macchina.Macchina;
import model.utente.Utente;

import java.util.List;

public abstract class DaoNoleggioAuto {

    public abstract void rentRequest(Utente utente,Macchina macchina);
    public abstract boolean checkInfo(Utente utente,Macchina macchina);
    public abstract List<Macchina> getUserCars(Utente utente);

}
