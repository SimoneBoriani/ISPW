package controller;

import bean.ProfileBean;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.daofactory.DaoFactory;
import model.utente.Utente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GestioneProfiloController {

    private static final Logger log = LogManager.getLogger(GestioneProfiloController.class);
    public final StringProperty statoOperazione = new SimpleStringProperty("");

    public void updateProfile(ProfileBean profileBean){

        Utente utente=new Utente();

        utente.setIdUser(profileBean.getId());
        utente.setNome(profileBean.getNome());
        utente.setCognome(profileBean.getCognome());
        utente.setUsername(profileBean.getUsername());
        utente.setUserPassword(profileBean.getPassword());

        DaoFactory.getDaoSingletonFactory().createUtenteDao().update(utente);

    }

    public void updateSaldo(ProfileBean bean){

        new Thread(() -> {

            try {
                log.info("Inizio deposito per ID: {}",bean.getId());

                Platform.runLater(() -> statoOperazione.set("Elaborazione deposito in corso... attendere 5 secondi."));
                Thread.sleep(5000);

                Utente utente = new Utente();

                utente.setIdUser(bean.getId());
                utente.setSaldo(bean.getSaldo());

                DaoFactory.getDaoSingletonFactory().createUtenteDao().update(utente);

                Platform.runLater(() -> statoOperazione.set("Deposito completato e salvato con successo dopo 5 secondi."));
                Thread.sleep(1500);

                Platform.runLater(() -> statoOperazione.set("FINE"));

            } catch (InterruptedException e) {
                Platform.runLater(() -> statoOperazione.set("Errore durante l'elaborazione."));
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}