package model.utente.dao;

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
        int generatedId = -1;

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
                    generatedId = rs.getInt(1);
                }
            }

            /*return new Utente(
                    generatedId,
                    utente.getUsername(),
                    utente.getUserPassword(),
                    utente.getNome(),
                    utente.getCognome()
            );*/
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Utente researchUser(Utente utente) throws SQLException {

        String sql = "SELECT * FROM utenti WHERE username = ?";

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
                return rs.next(); // Se c'è un risultato, rs.next() è true (utente trovato)
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Utente utente) {

        StringBuilder sql = new StringBuilder("UPDATE utenti SET ");
        List<Object> parametri = new ArrayList<>();
        Utente utenteSessione = SessionSingleton.getInstance().getUtenteCorrente();
        boolean first = true;

        if (utente.getUserPassword() != null && !utente.getUserPassword().trim().isEmpty()) {
            sql.append("password = ?");
            parametri.add(utente.getUserPassword());
            first = false;
        }

        if (utente.getNome() != null && !utente.getNome().trim().isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("nome = ?");
            utenteSessione.setNome(utente.getNome());
            parametri.add(utente.getNome());
            first = false;
        }

        if (utente.getCognome() != null && !utente.getCognome().trim().isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("cognome = ?");
            parametri.add(utente.getCognome());
            first = false;
        }

        if (utente.getAutoPossedute() > 0) {
            if (!first) sql.append(", ");
            sql.append("autopossedute = ?");
            parametri.add(utente.getAutoPossedute());
            first = false;
        }

        if (utente.getSaldo() >= 0.00) {
            if (!first) sql.append(", ");
            utenteSessione.setSaldo(utente.getSaldo());
            sql.append("saldo = ?");
            parametri.add(utente.getSaldo());
            first = false;
        }

        if (first) {
            System.out.println("Nessun campo da aggiornare.");
            return;
        }

        sql.append(" WHERE username = ?");
        parametri.add(utente.getUsername());

        System.out.println("Query generata: " + sql);

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement ps = session.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametri.size(); i++) {
                ps.setObject(i + 1, parametri.get(i));
            }

            int rows = ps.executeUpdate();
            System.out.println("Righe aggiornate: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}