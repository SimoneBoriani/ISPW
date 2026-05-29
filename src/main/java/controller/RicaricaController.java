package controller;

import bean.PaymentTransactionBean;
import com.stripe.exception.StripeException;
import model.daofactory.DaoFactory;
import model.utente.Utente;
import model.utente.dao.DaoUtente;
import service.StripeService;
import utils.SessionSingleton;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RicaricaController {

    private final StripeService stripe;
    private final DaoUtente daoUtente = DaoFactory.getDaoSingletonFactory().createUtenteDao();

    public RicaricaController(StripeService stripe) {
        this.stripe = stripe;
    }

    public boolean ricaricaSaldo(double importo) throws StripeException, IOException, ExecutionException, InterruptedException, TimeoutException {

        Utente utenteCorrente = SessionSingleton.getInstance().getUtenteCorrente();
        String username = utenteCorrente.getUsername();
        int idUser = utenteCorrente.getIdUser();

        PaymentTransactionBean tx = stripe.avviaRicarica(username, importo);

        if ("paid".equals(tx.getPaymentStatus())) {

            Utente utenteDaAggiornare = new Utente();
            utenteDaAggiornare.setIdUser(idUser);

            double nuovoSaldo = utenteCorrente.getSaldo() + tx.getAmount();
            utenteDaAggiornare.setSaldo(nuovoSaldo);

            daoUtente.update(utenteDaAggiornare);
            utenteCorrente.setSaldo(nuovoSaldo);

            return true;
        }

        return false;
    }
}