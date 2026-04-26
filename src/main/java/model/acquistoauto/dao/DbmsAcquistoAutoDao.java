package model.acquistoauto.dao;

import exceptions.GenericSystemException;
import model.macchina.Macchina;
import model.utente.Utente;
import utils.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbmsAcquistoAutoDao extends DaoAcquistoAuto {

    @Override
    public boolean checkInfo(Utente utente, Macchina macchina) {

        // 1. Query separate e corrette (assumo che l'id_transazione sia un SERIAL/AUTO_INCREMENT nel DB)
        String queryAuto = "SELECT prezzo FROM macchine WHERE auto_id = ?";

        // Assicurati che i nomi 'utenti' e 'saldo' (o 'portafoglio') corrispondano al tuo DB
        String queryUtente = "SELECT saldo FROM utenti WHERE id = ?";

        String insertVendita = "INSERT INTO vendite (id_utente, auto_id) VALUES (?, ?)";

        Connection con = ConnectionHandler.getInstance().getConnection();

        try {
            // DISABILITIAMO L'AUTOCOMMIT: Inizia la transazione sicura
            con.setAutoCommit(false);

            int prezzoAuto = 0;
            double soldiUtente = 0.0;

            // --- STEP 1: Controlliamo il prezzo REALE dell'auto nel DB ---
            try (PreparedStatement psAuto = con.prepareStatement(queryAuto)) {
                psAuto.setInt(1, macchina.getId());
                try (ResultSet rs = psAuto.executeQuery()) {
                    if (rs.next()) {
                        prezzoAuto = rs.getInt("prezzo");
                    } else {
                        return false; // L'auto non esiste più nel DB
                    }
                }
            }

            // --- STEP 2: Controlliamo i soldi REALI dell'utente nel DB ---
            try (PreparedStatement psUtente = con.prepareStatement(queryUtente)) {
                psUtente.setInt(1, utente.getIdUser()); // Assumo che l'oggetto Utente abbia un getId()
                try (ResultSet rs = psUtente.executeQuery()) {
                    if (rs.next()) {
                        soldiUtente = rs.getDouble("saldo");
                    } else {
                        return false; // L'utente non esiste
                    }
                }
            }

            // --- STEP 3: Controllo Logico (Ha i soldi?) ---
            if (soldiUtente >= prezzoAuto) {

                // --- STEP 4: Inserimento nella tabella vendite ---
                try (PreparedStatement psVendita = con.prepareStatement(insertVendita)) {
                    psVendita.setInt(1, utente.getIdUser());
                    psVendita.setInt(2, macchina.getId());
                    psVendita.executeUpdate();
                }

                /* * OPZIONALE (Ma consigliato per la coerenza del sistema):
                 * Qui dovresti aggiungere anche le query per:
                 * 1. Sottrarre i soldi all'utente (UPDATE utenti SET saldo = saldo - ? WHERE id = ?)
                 * 2. Rimuovere l'auto dal catalogo (DELETE FROM macchine WHERE auto_id = ?) o segnarla come 'venduta'.
                 */

                // Tutto è andato bene! CONFERMIAMO LA TRANSAZIONE
                con.commit();
                return true;

            } else {
                // L'utente non ha i soldi, chiudiamo senza fare l'insert
                return false;
            }

        } catch (SQLException e) {
            // SE QUALCOSA ESPLODE: Annulliamo tutte le modifiche fatte finora
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                throw new GenericSystemException("Errore critico durante il rollback: " + ex.getMessage());
            }
            throw new GenericSystemException("Errore durante l'acquisto: " + e.getMessage());

        } finally {
            // Ripristiniamo sempre lo stato della connessione alla fine
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new GenericSystemException("Impossibile ripristinare l'autocommit: " + e.getMessage());
            }
        }
    }
}
