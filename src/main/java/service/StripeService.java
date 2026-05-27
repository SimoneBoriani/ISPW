package service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import bean.PaymentTransactionBean;
import exceptions.PaymentFailedException;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;

public class StripeService {

    private final int callbackPort;

    public StripeService(String stripeSecretKey, int callbackPort) {
        Stripe.apiKey = stripeSecretKey;
        this.callbackPort = callbackPort;
    }

    /**
     * Avvia il flusso di ricarica:
     * 1. Crea Checkout Session su Stripe
     * 2. Salva transazione PENDING su DB (chiamato dal controller)
     * 3. Apre browser
     * 4. Aspetta callback
     * 5. Verifica stato pagamento
     *
     * @return PaymentTransactionBean con stato finale (paid/expired/failed)
     */
    public PaymentTransactionBean avviaRicarica(String username, double importo) throws Exception {

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
                                                    .setUnitAmount((long)(importo * 100)) // Stripe vuole CENTESIMI
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Ricarica saldo KrustyNoDusty")
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
            tx.setPaymentStatus(check.getPaymentStatus()); // "paid", "unpaid", etc.
            return tx;

        } finally {
            server.stop();
        }
    }
}