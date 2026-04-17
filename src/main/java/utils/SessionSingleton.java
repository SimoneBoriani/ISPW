package utils;

import model.utente.Utente;

public class SessionSingleton {

    private static SessionSingleton instance;

    private Utente utenteCorrente;

    private SessionSingleton() {
    }

    public static SessionSingleton getInstance() {
        if (instance == null) {
            instance = new SessionSingleton();
        }
        return instance;
    }

    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public Utente getUtenteCorrente() {
        return this.utenteCorrente;
    }

    public void logout() {
        this.utenteCorrente = null;
    }

    public boolean isUserLoggedIn() {
        return this.utenteCorrente != null;
    }
}
