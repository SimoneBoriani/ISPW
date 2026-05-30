package model.utente.dao;

import model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class DemoDaoUtente extends DaoUtente {

    private static final List<Utente> users = new ArrayList<>();
    private static int counter = 1;

    @Override
    public void insertUtente(Utente utente) {

        if (utente.getUsername() == null) {
            throw new NullPointerException("L'username non può essere nullo o vuoto.");
        }
        if (utente.getUserPassword() == null) {
            throw new NullPointerException("La password non può essere nulla o vuota.");
        }
        String ruolo = users.isEmpty() ? "ADMIN" : "USER";
        utente.setRuolo(ruolo);

        synchronized (DemoDaoUtente.class) {
            utente.setIdUser(counter);
            users.add(utente);
            counter++;
        }
    }

    @Override
    public Utente researchUser(Utente utente) {

        for (Utente u : users) {
            if (u.getUsername().equals(utente.getUsername())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public boolean authenticateUser(Utente utente) {

        for (Utente u : users) {
            if (u.getUsername().equals(utente.getUsername()) &&
                    u.getUserPassword().equals(utente.getUserPassword())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(Utente utente) {
        if (utente == null) {
            return;
        }

        users.stream()
                .filter(u -> u.getIdUser() == utente.getIdUser())
                .findFirst()
                .ifPresent(u -> {

                    if (isNotBlank(utente.getUsername())) {
                        u.setUsername(utente.getUsername());
                    }
                    if (isNotBlank(utente.getNome())) {
                        u.setNome(utente.getNome());
                    }
                    if (isNotBlank(utente.getCognome())) {
                        u.setCognome(utente.getCognome());
                    }

                    if (utente.getSaldo() > 0) {
                        u.setSaldo(u.getSaldo() + utente.getSaldo());
                    }
                });
    }

    @Override
    public void aggiornaStatoPatente(int idUser, boolean stato) {
        users.stream()
                .filter(u -> u.getIdUser() == idUser)
                .findFirst()
                .ifPresent(u -> u.setVerificato(stato));
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }
}
