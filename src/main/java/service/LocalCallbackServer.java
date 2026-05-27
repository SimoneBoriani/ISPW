package service;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class LocalCallbackServer {

    private final int port;
    private HttpServer server;
    private final CompletableFuture<String> sessionIdFuture = new CompletableFuture<>();
    private static final String SESSID="session_id=";

    public LocalCallbackServer(int port) {
        this.port = port;
    }

    public CompletableFuture<String> start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", port), 0);

        server.createContext("/success", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String sessionId = null;
            if (query != null && query.contains(SESSID)) {
                for (String pair : query.split("&")) {
                    if (pair.startsWith(SESSID)) {
                        sessionId = pair.substring(SESSID.length());
                    }
                }
            }
            sendHtml(exchange,
                    "<h2>✅ Richiesta Processata con Successo!</h2>" +
                            "<p>Puoi chiudere questa finestra e tornare a BoroRental.</p>");
            if (sessionId != null) sessionIdFuture.complete(sessionId);
        });

        server.createContext("/cancel", exchange -> {
            sendHtml(exchange, "<h2>❌ Richiesta Annulata</h2><p>Torna all'app.</p>");
            sessionIdFuture.complete(null);
        });

        server.setExecutor(null);
        server.start();
        return sessionIdFuture;
    }

    public void stop() {
        if (server != null) server.stop(1);
    }

    private void sendHtml(com.sun.net.httpserver.HttpExchange ex, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}