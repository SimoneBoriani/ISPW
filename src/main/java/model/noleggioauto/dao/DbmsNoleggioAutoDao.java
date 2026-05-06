package model.noleggioauto.dao;

import exceptions.GenericSystemException;
import model.macchina.Macchina;
import model.utente.Utente;
import utils.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbmsNoleggioAutoDao extends DaoNoleggioAuto {

    @Override
    public void rentRequest(Utente utente, Macchina macchina) {

        String queryNoleggio = "INSERT INTO noleggi_attivi (id_utente, auto_id) VALUES (?, ?)";
        String querySaldo = "UPDATE utenti SET saldo = saldo - ? WHERE id = ?";
        String queryNascondiAuto = "UPDATE macchine SET disponibile = false WHERE auto_id = ?";

        Connection connection = ConnectionHandler.getInstance().getConnection();

        try {

            connection.setAutoCommit(false);

            try (PreparedStatement stmtNoleggio = connection.prepareStatement(queryNoleggio)) {
                stmtNoleggio.setInt(1, utente.getIdUser());
                stmtNoleggio.setInt(2, macchina.getId());
                stmtNoleggio.executeUpdate();
            }
            try (PreparedStatement stmtSaldo = connection.prepareStatement(querySaldo)) {
                stmtSaldo.setInt(1, macchina.getPrezzo());
                stmtSaldo.setInt(2, utente.getIdUser());
                stmtSaldo.executeUpdate();
            }
            try (PreparedStatement stmtNascondi = connection.prepareStatement(queryNascondiAuto)) {
                stmtNascondi.setInt(1, macchina.getId());
                stmtNascondi.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {

            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new GenericSystemException("Errore critico nel rollback: " + ex.getMessage());
            }
            throw new GenericSystemException("Errore durante il noleggio: " + e.getMessage());

        } finally {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new GenericSystemException("Errore nel ripristino della connessione: " + e.getMessage());
            }
        }
    }


    @Override
    public boolean checkInfo(Utente utente, Macchina macchina) {

        String queryAuto = "SELECT prezzo FROM macchine WHERE auto_id = ?";
        String queryUtente = "SELECT saldo FROM utenti WHERE id = ?";

        Connection con = ConnectionHandler.getInstance().getConnection();

        try {

            int prezzoAuto = 0;
            double soldiUtente = 0.0;

            try (PreparedStatement psAuto = con.prepareStatement(queryAuto)) {
                psAuto.setInt(1, macchina.getId());
                try (ResultSet rs = psAuto.executeQuery()) {
                    if (rs.next()) {
                        prezzoAuto = rs.getInt("prezzo");
                    } else {
                        return false;
                    }
                }
            }

            try (PreparedStatement psUtente = con.prepareStatement(queryUtente)) {

                psUtente.setInt(1, utente.getIdUser());
                try (ResultSet rs = psUtente.executeQuery()) {
                    if (rs.next()) {
                        soldiUtente = rs.getDouble("saldo");
                    } else {
                        return false;
                    }
                }
            }

            return soldiUtente >= prezzoAuto;

        } catch (SQLException e) {

            throw new GenericSystemException("Errore DB durante il controllo noleggio: " + e.getMessage());
        }
    }

    @Override
    public List<Macchina> getUserCars(Utente utente) {

        List<Macchina> autoNoleggiate = new ArrayList<>();

        String query = "SELECT m.* FROM macchine m JOIN noleggi_attivi n ON m.auto_id = n.auto_id WHERE n.id_utente = ?";

        Connection conn = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, utente.getIdUser());

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    Macchina m = new Macchina();
                    m.setId(rs.getInt("auto_id"));

                    m.setMarca(rs.getString("marca"));
                    m.setModello(rs.getString("modello"));
                    m.setAnno(rs.getInt("anno"));
                    m.setTrasmissione(rs.getString("trasmissione"));
                    m.setPrezzo(rs.getInt("prezzo"));
                    m.setAlimentazione(rs.getString("alimentazione"));
                    m.setTipologia(rs.getString("tipologia"));
                    m.setPosti(rs.getInt("posti"));
                    m.setImageUrl(rs.getString("immagine_url"));

                    autoNoleggiate.add(m);
                }
            }

        } catch (SQLException e) {
            throw new GenericSystemException("Errore durante il recupero delle auto noleggiate: " + e.getMessage());
        }

        return autoNoleggiate;
    }
}