package controller;

import bean.SegnalazioneBean;
import model.daofactory.DaoFactory;
import model.notifiche.Notifica;

import java.util.List;

public class NotificheController {

    public NotificheController() {
    //Costruttore
    }

    public void inviaMessaggioAdAdmin(String idUtente,String auto ,String testo) {

        if (testo == null || testo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il testo del messaggio non può essere vuoto.");
        }

        Notifica messaggio = new Notifica(
                idUtente,
                "1",
                testo,
                Notifica.Tipo.MESSAGGIO
        );

        messaggio.setAuto(auto);

        DaoFactory.getDaoSingletonFactory().createNotificheDao().inserisci(messaggio);
    }

    public void generaNotificaSistema(String idUtenteDestinatario, String testo) {
        Notifica notifica = new Notifica(
                "SISTEMA",
                idUtenteDestinatario,
                testo,
                Notifica.Tipo.SISTEMA
        );
        DaoFactory.getDaoSingletonFactory().createNotificheDao().inserisci(notifica);
    }

    public List<Notifica> getStoricoNotifiche(SegnalazioneBean bean) {
        return DaoFactory.getDaoSingletonFactory().createNotificheDao().getComunicazioniPerDestinatario(String.valueOf(bean.getUtente().getIdUser()));
    }

    public List<Notifica> getNotificheDaLeggere(SegnalazioneBean bean) {
        return DaoFactory.getDaoSingletonFactory().createNotificheDao().getNonLette(String.valueOf(bean.getUtente().getIdUser()));
    }

    public void apriNotifica(SegnalazioneBean bean) {
        DaoFactory.getDaoSingletonFactory().createNotificheDao().segnaComeLetta(bean.getId());
    }

    public void eliminaNotifica(SegnalazioneBean bean) {
        DaoFactory.getDaoSingletonFactory().createNotificheDao().eliminaNotifica(bean.getId());
    }
}