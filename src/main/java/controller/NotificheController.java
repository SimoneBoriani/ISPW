package controller;

import bean.NotificaBean;
import exceptions.NotificationErrorException;
import model.daofactory.DaoFactory;
import model.notifiche.Notifica;

import java.util.List;

public class NotificheController {

    public NotificheController() {
        //Costruttore
    }

    public void inviaMessaggioAdAdmin(NotificaBean bean) {

        if (bean == null || bean.getUtente() == null) {
            throw new IllegalArgumentException("NotificaBean e Utente non possono essere null");
        }

        Notifica messaggio = new Notifica(
                String.valueOf(bean.getUtente().getIdUser()),
                "1",
                bean.getMsg(),
                Notifica.Tipo.MESSAGGIO
        );
        messaggio.setAuto(bean.getMacchina().getId()+" "+bean.getMacchina().getModello()+" "+bean.getMacchina().getMarca());
        DaoFactory.getDaoSingletonFactory().createNotificheDao().inserisci(messaggio);
    }

    public void generaNotificaSistema(NotificaBean bean) {

        if (bean == null || bean.getUtente() == null) {
            throw new IllegalArgumentException("NotificaBean e Utente non possono essere null");
        }

        Notifica notifica = new Notifica(
                "SISTEMA",
                String.valueOf(bean.getUtente().getIdUser()),
                bean.getMsg(),
                Notifica.Tipo.SISTEMA
        );

        try {
            DaoFactory.getDaoSingletonFactory().createNotificheDao().inserisci(notifica);
        } catch (Exception e) {
            throw new NotificationErrorException("Impossibile salvare la notifica di sistema:", e);
        }
    }

    public List<Notifica> getStoricoNotifiche(NotificaBean bean) {
        return DaoFactory.getDaoSingletonFactory().createNotificheDao().getComunicazioniPerDestinatario(String.valueOf(bean.getUtente().getIdUser()));
    }

    public List<Notifica> getNotificheDaLeggere(NotificaBean bean) {
        return DaoFactory.getDaoSingletonFactory().createNotificheDao().getNonLette(String.valueOf(bean.getUtente().getIdUser()));
    }

    public void apriNotifica(NotificaBean bean) {
        DaoFactory.getDaoSingletonFactory().createNotificheDao().segnaComeLetta(Integer.parseInt((bean.getId())));
    }

    public void eliminaNotifica(NotificaBean bean) {
        DaoFactory.getDaoSingletonFactory().createNotificheDao().eliminaNotifica(Integer.parseInt(bean.getId()));
    }
}