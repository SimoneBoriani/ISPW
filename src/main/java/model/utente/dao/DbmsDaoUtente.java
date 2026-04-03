package model.utente.dao;

import app.SessionSingleton;
import bean.LoginBean;
import model.utente.Utente;
import utils.ConnectionHandler; // <-- Importa il tuo nuovo ConnectionHandler

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbmsDaoUtente extends DaoUtente {

    public static Utente insertUtente(LoginBean loginBean) throws SQLException {

        String sql = "INSERT INTO utenti (username,password,nome,cognome) VALUES (?,?,?,?)";
        int generatedId = -1;

        // 1. Recuperiamo la connessione dal Singleton (fuori dal try-with-resources)
        Connection session = ConnectionHandler.getInstance().getConnection();

        // 2. Il try chiuderà automaticamente solo lo statement
        try (PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, loginBean.getUsername());
            statement.setString(2, loginBean.getPassword());

            if (loginBean.getNome() != null) {
                statement.setString(3, loginBean.getNome());
            } else {
                statement.setNull(3, Types.VARCHAR);
            }

            if (loginBean.getCognome() != null) {
                statement.setString(4, loginBean.getCognome());
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

            return new Utente(
                    generatedId,
                    loginBean.getUsername(),
                    loginBean.getPassword(),
                    loginBean.getNome(),
                    loginBean.getCognome()
            );
        }
    }

    public static Utente researchUser(LoginBean loginBean) throws SQLException {

        String sql = "SELECT * FROM utenti WHERE username = ?";

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, loginBean.getUsername());

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

    public static boolean authenticateUser(LoginBean loginBean) throws SQLException {

        String sql = "SELECT username, password FROM utenti WHERE username = ? and password = ?";

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, loginBean.getUsername());
            statement.setString(2, loginBean.getPassword());

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next(); // Se c'è un risultato, rs.next() è true (utente trovato)
            }
        }
    }

    public static void update(LoginBean loginBean) throws SQLException {

        StringBuilder sql = new StringBuilder("UPDATE utenti SET ");
        List<Object> parametri = new ArrayList<>();
        Utente utenteSessione = SessionSingleton.getInstance().getUtenteCorrente();
        boolean first = true;

        if (loginBean.getPassword() != null && !loginBean.getPassword().trim().isEmpty()) {
            sql.append("password = ?");
            parametri.add(loginBean.getPassword());
            first = false;
        }

        if (loginBean.getNome() != null && !loginBean.getNome().trim().isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("nome = ?");
            utenteSessione.setNome(loginBean.getNome());
            parametri.add(loginBean.getNome());
            first = false;
        }

        if (loginBean.getCognome() != null && !loginBean.getCognome().trim().isEmpty()) {
            if (!first) sql.append(", ");
            sql.append("cognome = ?");
            parametri.add(loginBean.getCognome());
            first = false;
        }

        if (loginBean.getAutoPossedute() > 0) {
            if (!first) sql.append(", ");
            sql.append("autopossedute = ?");
            parametri.add(loginBean.getAutoPossedute());
            first = false;
        }

        if (loginBean.getSaldo() >= 0.00) {
            if (!first) sql.append(", ");
            utenteSessione.setSaldo(loginBean.getSaldo());
            sql.append("saldo = ?");
            parametri.add(loginBean.getSaldo());
            first = false;
        }

        if (first) {
            System.out.println("Nessun campo da aggiornare.");
            return;
        }

        sql.append(" WHERE username = ?");
        parametri.add(loginBean.getUsername());

        System.out.println("Query generata: " + sql);

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement ps = session.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametri.size(); i++) {
                ps.setObject(i + 1, parametri.get(i));
            }

            int rows = ps.executeUpdate();
            System.out.println("Righe aggiornate: " + rows);
        }
    }
}