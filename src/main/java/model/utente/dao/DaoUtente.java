package model.utente.dao;

import model.utente.Utente;

import java.sql.SQLException;

public abstract class DaoUtente {

    public abstract void insertUtente(Utente utente);
    public abstract Utente researchUser(Utente utente) throws SQLException;
    public abstract boolean authenticateUser(Utente utente);
    public abstract void update(Utente utente);

}
