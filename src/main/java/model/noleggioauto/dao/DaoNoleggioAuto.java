package model.noleggioauto.dao;

import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class DaoNoleggioAuto {

    public abstract void rentRequest(Utente utente,Macchina macchina,int giorni);
    public abstract List<NoleggioAuto> getUserCars(Utente utente);
    public abstract void terminaNoleggio(int id,String motivo);
    public abstract void sbloccaAutoScadute();
    public abstract List<NoleggioAuto> getRented();
    public abstract Map<LocalDate,Double> getProfittiPerData();

}
