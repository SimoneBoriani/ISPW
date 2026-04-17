package model.utente.dao;

import model.utente.Utente;
import java.util.ArrayList;
import java.util.List;

public class DemoDaoUtente extends DaoUtente {

    private static final List<Utente> users = new ArrayList<>();
    private static int counter = 0;

    @Override
    public void insertUtente(Utente utente) {

        if (counter == 0) {
            utente.setRuolo("ADMIN");
        } else {
            utente.setRuolo("USER");
        }

        users.add(utente);
        counter++;
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
        //implementare
    }
}