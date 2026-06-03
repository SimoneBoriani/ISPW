package service;

import bean.PaymentTransactionBean;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import exceptions.PaymentFailedException;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class StripeService {

    private final int callbackPort;

    public StripeService(String stripeSecretKey, int callbackPort) {
        Stripe.apiKey = stripeSecretKey;
        this.callbackPort = callbackPort;
    }

    public PaymentTransactionBean avviaRicarica(String username, double importo)
            throws IOException, StripeException, ExecutionException, InterruptedException, TimeoutException {
        return avviaCheckoutSession(username, "Ricarica saldo BoroRental", importo);
    }

    public PaymentTransactionBean avviaPagamentoNoleggio(String username, String descrizioneAuto, double importo)
            throws IOException, StripeException, ExecutionException, InterruptedException, TimeoutException {
        return avviaCheckoutSession(username, "Noleggio " + descrizioneAuto + " - BoroRental", importo);
    }

    private PaymentTransactionBean avviaCheckoutSession(String username, String productName, double importo)
            throws IOException, StripeException, ExecutionException, InterruptedException, TimeoutException {

        if (importo <= 0) {
            throw new PaymentFailedException("L'importo deve essere maggiore di zero.");
        }

        LocalCallbackServer server = new LocalCallbackServer(callbackPort);
        var future = server.start();

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:" + callbackPort + "/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:" + callbackPort + "/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount((long) (importo * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(productName)
                                                                    .build())
                                                    .build())
                                    .build())
                    .putMetadata("username", username)
                    .build();

            Session session = Session.create(params);

            PaymentTransactionBean tx = new PaymentTransactionBean(
                    session.getId(), username, importo, "EUR", "pending", LocalDateTime.now());

            Desktop.getDesktop().browse(URI.create(session.getUrl()));

            String returnedSessionId = future.get(10, java.util.concurrent.TimeUnit.MINUTES);

            if (returnedSessionId == null) {
                tx.setPaymentStatus("cancelled");
                return tx;
            }

            Session check = Session.retrieve(returnedSessionId);
            tx.setPaymentStatus(check.getPaymentStatus());
            return tx;

        } finally {
            server.stop();
        }
    }
}