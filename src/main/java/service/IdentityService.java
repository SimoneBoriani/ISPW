package service;

import com.stripe.Stripe;
import com.stripe.model.identity.VerificationSession;
import com.stripe.param.identity.VerificationSessionCreateParams;

import java.awt.Desktop;
import java.net.URI;

public class IdentityService {

    private final int callbackPort;

    public IdentityService(String stripeSecretKey, int callbackPort) {
        Stripe.apiKey = stripeSecretKey;
        this.callbackPort = callbackPort;
    }
    public String avviaVerificaPatente(String idUser) throws Exception {

        LocalCallbackServer server = new LocalCallbackServer(callbackPort);
        var future = server.start();

        try {
            VerificationSessionCreateParams params = VerificationSessionCreateParams.builder()
                    .setType(VerificationSessionCreateParams.Type.DOCUMENT)
                    .setOptions(
                            VerificationSessionCreateParams.Options.builder()
                                    .setDocument(
                                            VerificationSessionCreateParams.Options.Document.builder()
                                                    .addAllowedType(VerificationSessionCreateParams.Options.Document.AllowedType.DRIVING_LICENSE)
                                                    .setRequireIdNumber(true)
                                                    .setRequireMatchingSelfie(true)
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("idUser", idUser)
                    .setReturnUrl("http://localhost:" + callbackPort + "/success?session_id=verifica_completata")
                    .build();

            VerificationSession session = VerificationSession.create(params);

            Desktop.getDesktop().browse(URI.create(session.getUrl()));

            String returnedSignal = future.get(10, java.util.concurrent.TimeUnit.MINUTES);

            if (returnedSignal == null) {
                return "cancelled";
            }

            VerificationSession check = VerificationSession.retrieve(session.getId());
            return check.getStatus();

        } finally {
            server.stop();
        }
    }
}