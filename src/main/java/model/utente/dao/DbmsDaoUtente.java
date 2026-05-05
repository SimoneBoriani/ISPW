package model.utente.dao;

import exceptions.GenericSystemException;
import utils.SessionSingleton;
import model.utente.Utente;
import utils.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbmsDaoUtente extends DaoUtente {

    @Override
    public void insertUtente(Utente utente){

        String sql = "INSERT INTO utenti (username,password,nome,cognome) VALUES (?,?,?,?)";

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, utente.getUsername());
            statement.setString(2, utente.getUserPassword());

            if (utente.getNome() != null) {
                statement.setString(3, utente.getNome());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            if (utente.getCognome() != null) {
                statement.setString(4, utente.getCognome());
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                     rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
    }

    @Override
    public Utente researchUser(Utente utente) throws SQLException {

        String sql = "SELECT *" + " FROM utenti WHERE username = ?";

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, utente.getUsername());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String nome = rs.getString("nome");
                    String passwordDb = rs.getString("password");
                    String cognome = rs.getString("cognome");
                    String ruolo = rs.getString("ruolo");
                    int autoPossedute = rs.getInt("autopossedute");
                    int saldo = rs.getInt("saldo");

                    return new Utente(
                            id,
                            username,
                            passwordDb,
                            nome,
                            cognome,
                            autoPossedute,
                            saldo,
                            ruolo
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean authenticateUser(Utente utente) {

        String sql = "SELECT username, password FROM utenti WHERE username = ? and password = ?";

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, utente.getUsername());
            statement.setString(2, utente.getUserPassword());

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
    }

    @Override
    public void update(Utente utente) {
        List<Object> parametri = new ArrayList<>();
        String sql = costruisciQueryUpdate(utente, parametri);

        if (sql == null) {
            return;
        }

        int rowsAffected = eseguiQuery(sql, parametri);

        if (rowsAffected > 0) {
            sincronizzaSessione(utente);
        }
    }

    private String costruisciQueryUpdate(Utente utente, List<Object> parametri) {
        List<String> setClauses = new ArrayList<>();

        if (utente.getUsername() != null && !utente.getUsername().trim().isEmpty()) {
            setClauses.add("username = ?");
            parametri.add(utente.getUsername());
        }
        if (utente.getUserPassword() != null && !utente.getUserPassword().trim().isEmpty()) {
            setClauses.add("password = ?");
            parametri.add(utente.getUserPassword());
        }
        if (utente.getNome() != null && !utente.getNome().trim().isEmpty()) {
            setClauses.add("nome = ?");
            parametri.add(utente.getNome());
        }
        if (utente.getCognome() != null && !utente.getCognome().trim().isEmpty()) {
            setClauses.add("cognome = ?");
            parametri.add(utente.getCognome());
        }
        if (utente.getAutoPossedute() > 0) {
            setClauses.add("autopossedute = ?");
            parametri.add(utente.getAutoPossedute());
        }
        if (utente.getSaldo() >= 0.00) {
            setClauses.add("saldo = ?");
            parametri.add(utente.getSaldo());
        }

        if (setClauses.isEmpty()) {
            return null;
        }

        String sql = "UPDATE utenti SET " + String.join(", ", setClauses) + " WHERE id = ?";
        parametri.add(utente.getIdUser());

        return sql;

    }

    private int eseguiQuery(String sql, List<Object> parametri) {
        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement ps = session.prepareStatement(sql)) {
            for (int i = 0; i < parametri.size(); i++) {
                ps.setObject(i + 1, parametri.get(i));
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
    }

    private void sincronizzaSessione(Utente utente) {

        Utente utenteSessione = SessionSingleton.getInstance().getUtenteCorrente();

        if (utente.getUsername() != null && !utente.getUsername().trim().isEmpty()) {
            utenteSessione.setUsername(utente.getUsername());
        }
        if (utente.getNome() != null && !utente.getNome().trim().isEmpty()) {
            utenteSessione.setNome(utente.getNome());
        }
        if (utente.getSaldo() >= 0.00) {
            utenteSessione.setSaldo(utente.getSaldo());
        }
        if(utente.getCognome() != null && !utente.getCognome().trim().isEmpty()) {
            utenteSessione.setCognome(utente.getCognome());
        }
    }
}