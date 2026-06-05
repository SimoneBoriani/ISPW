package controller;

import bean.NoleggioAutoBean;
import bean.NotificaBean;
import com.stripe.exception.StripeException;
import exceptions.DocsNotValidException;
import exceptions.PaymentFailedException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.utente.Utente;
import model.duratacontrattuale.*;
import service.StripeService;
import utils.ConfigLoader;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class NoleggioController {

    private final NotificheController notificheController = ControllerFactory.getGraphicalSingletonFactory().createNotificheController();

    public double calcolaTotale(Macchina auto, int giorni) {
        PianoNoleggio pianoScelto = determinaPiano(giorni);
        return pianoScelto.calcolaPrezzo(auto.getPrezzo(), giorni);
    }

    public Macchina getAuto() {
        return SessionSingleton.getInstance().getAutoSelezionata();
    }

    public void clear(){SessionSingleton.getInstance().setAutoSelezionata(null);
    }

    public void processaNoleggio(NoleggioAutoBean bean) {

        Macchina auto = bean.getMacchina();
        Utente utente = bean.getRenter();
        int giorni = bean.getGiorni();

        if (giorni < 0) {
            throw new IllegalArgumentException("Giorni non devono essere negativi.");
        }

        if (Boolean.FALSE.equals(utente.getVerificato()) || utente.getVerificato() == null) {
            throw new DocsNotValidException("Impossibile noleggiare: Patente non verificata.");
        }

        double totale = calcolaTotale(auto, giorni);

        if (utente.getSaldo() < totale) {
            throw new IllegalArgumentException("Saldo insufficiente");
        }

        bean.getMacchina().setPrezzo(totale);

        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().rentRequest(utente, auto, giorni);
    }

    public void processaNoleggioStripe(NoleggioAutoBean bean) throws StripeException, IOException, ExecutionException, InterruptedException, TimeoutException {

        Macchina auto = bean.getMacchina();
        Utente utente = bean.getRenter();
        int giorni = bean.getGiorni();

        if (giorni < 0) {
            throw new IllegalArgumentException("Giorni non devono essere negativi.");
        }

        if (Boolean.FALSE.equals(utente.getVerificato()) || utente.getVerificato() == null) {
            throw new DocsNotValidException("Impossibile noleggiare: Patente non verificata.");
        }

        double totale = calcolaTotale(auto, giorni);

        StripeService stripe = new StripeService(
                ConfigLoader.get("stripe.secret.key"),
                ConfigLoader.getInt("stripe.success.port")
        );

        var tx = stripe.avviaPagamentoNoleggio(
                utente.getUsername(),
                auto.getMarca() + " " + auto.getModello(),
                totale
        );

        if (!"paid".equals(tx.getPaymentStatus())) {
            throw new PaymentFailedException("Pagamento Stripe non completato. Stato: " + tx.getPaymentStatus());
        }

        bean.getMacchina().setPrezzo(totale);
        bean.setPagamentoEsternoConfermato(true);

        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().rentRequestEsternoConfermato(utente, auto, giorni);
    }

    public PianoNoleggio determinaPiano(int giorni) {

        if (giorni <= 0) {
            throw new IllegalArgumentException("Il numero di giorni deve essere maggiore di zero.");
        }

        if (giorni <= 30) {
            return new NoleggioBreveTermine();
        } else if (giorni <= 364) {
            return new NoleggioMedioTermine();
        } else if (giorni <= 700) {
            return new NoleggioLungoTermine();
        } else {
            throw new IllegalArgumentException("Il limite massimo di noleggio è 700 giorni.");
        }
    }

    public void segnalazione(NotificaBean bean) {
        if (bean != null) {
            notificheController.inviaMessaggioAdAdmin(bean);
        } else throw new NullPointerException("Segnalazione non trovata");
    }

    public void notificaSistema(NotificaBean bean) {
        if (bean != null) {
            notificheController.generaNotificaSistema(bean);
        } else throw new NullPointerException("Segnalazione non trovata");
    }
}