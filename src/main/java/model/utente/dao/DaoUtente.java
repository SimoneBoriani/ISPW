package model.utente.dao;


import bean.LoginBean;
import model.daofactory.DaoFactory;
import model.utente.Utente;

import java.sql.*;

public class DaoUtente extends DaoFactory {

    public static Utente insertUtente(LoginBean loginBean) throws SQLException {

        String sql = "INSERT INTO utenti (username,password,nome,cognome) VALUES (?,?,?,?)";
        int generatedId = -1;

        try (Connection session = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, loginBean.getUsername());
            statement.setString(2, loginBean.getPassword());

            if (loginBean.getNome() != null) {
                statement.setString(3, loginBean.getNome());
            }else{
                statement.setNull(3, Types.VARCHAR);
            }

            if (loginBean.getCognome() != null) {
                statement.setString(4, loginBean.getCognome());
            }else{
                statement.setNull(4, Types.VARCHAR);
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

            Utente utente=new Utente(
                    generatedId,
                    loginBean.getUsername(),
                    loginBean.getPassword(),
                    loginBean.getNome(),
                    loginBean.getCognome()
            );
            return utente;
        }


            }
    public Utente researchUser(LoginBean loginBean) throws SQLException {

        String sql = "SELECT * FROM utenti WHERE username = ? AND password = ?";

        try (Connection session = DriverManager.getConnection(url, user, password);

             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, loginBean.getUsername());
            statement.setString(2, loginBean.getPassword());

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

                    Utente utente = new Utente(
                            id,
                            username,
                            passwordDb,
                            nome,
                            cognome,
                            autoPossedute,
                            saldo,
                            ruolo
                    );

                    return utente;
                }
            }
        }
        return null;
    }
}

