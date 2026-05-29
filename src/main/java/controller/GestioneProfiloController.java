package controller;

import bean.ProfileBean;
import com.stripe.exception.StripeException;
import model.daofactory.DaoFactory;
import model.utente.Utente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.IdentityService;
import utils.ConfigLoader;
import utils.SessionSingleton;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class GestioneProfiloController {

    private static final Logger log = LogManager.getLogger(GestioneProfiloController.class);

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

                log.info("Inizio deposito per ID: {}",bean.getId());

                Utente utente = new Utente();

                utente.setIdUser(bean.getId());
                utente.setSaldo(bean.getSaldo());

                DaoFactory.getDaoSingletonFactory().createUtenteDao().update(utente);
    }

    public String avviaVerificaPatente(ProfileBean bean) throws StripeException, IOException, ExecutionException, InterruptedException, TimeoutException {

        IdentityService service = new IdentityService(
                ConfigLoader.get("stripe.secret.key"),
                ConfigLoader.getInt("stripe.success.port")
        );

        String userId = String.valueOf(bean.getId());
        return service.avviaVerificaPatente(userId);
    }


    public void completaVerificaPatente(ProfileBean bean) {
        DaoFactory.getDaoSingletonFactory().createUtenteDao().aggiornaStatoPatente(bean.getId(), true);
        SessionSingleton.getInstance().getUtenteCorrente().setVerificato(true);
    }
}