package model.utente.dao;

import model.utente.Utente;

import java.util.ArrayList;
import java.util.List;

public class DemoDaoUtente extends DaoUtente {

    private static final List<Utente> users = new ArrayList<>();
    private static int counter = 0;

    @Override
    public void insertUtente(Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente da inserire non può essere null");
        }

        String ruolo = users.isEmpty() ? "ADMIN" : "USER";
        utente.setRuolo(ruolo);

        synchronized (DemoDaoUtente.class) {
            utente.setIdUser(counter++);
            users.add(utente);
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
            if (u.getUsername().equals(utente.getUsername())) {
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

    private boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }
}