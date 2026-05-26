package model.notifiche.dao;

import model.notifiche.Notifica;

import java.util.List;

public abstract class DaoNotifica {

    public abstract void inserisci(Notifica notifica);
    public abstract List<Notifica> getComunicazioniPerDestinatario(String destinatario);
    public abstract List<Notifica> getNonLette(String destinatario);
    public abstract void segnaComeLetta(int id);
    public abstract void eliminaNotifica(int id);

}