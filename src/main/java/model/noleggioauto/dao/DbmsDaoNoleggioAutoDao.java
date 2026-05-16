package model.noleggioauto.dao;

import exceptions.GenericSystemException;
import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;
import utils.ConnectionHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DbmsDaoNoleggioAutoDao extends DaoNoleggioAuto {

    private static final String COL_AUTO_ID = "auto_id";
    private static final String COL_MARCA = "marca";
    private static final String COL_MODELLO = "modello";
    private static final String COL_IMMAGINE_URL = "immagine_url";
    private static final String COL_DATA_FINE = "data_fine";

    @Override
    public void rentRequest(Utente utente, Macchina macchina, int giorni) {
        String queryNoleggio = "INSERT INTO noleggi (id_utente, auto_id, data_fine, prezzo_totale, stato) VALUES (?, ?, ?, ?, 'ATTIVO')";
        String querySaldo = "UPDATE utenti SET saldo = saldo - ? WHERE id = ?";
        String queryNascondiAuto = "UPDATE macchine SET disponibile = false WHERE auto_id = ?";

        Connection connection = ConnectionHandler.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtNoleggio = connection.prepareStatement(queryNoleggio);
                 PreparedStatement stmtSaldo = connection.prepareStatement(querySaldo);
                 PreparedStatement stmtNascondi = connection.prepareStatement(queryNascondiAuto)) {

                stmtNoleggio.setInt(1, utente.getIdUser());
                stmtNoleggio.setInt(2, macchina.getId());
                LocalDate dataScadenza = LocalDate.now().plusDays(giorni);
                stmtNoleggio.setDate(3, Date.valueOf(dataScadenza));
                stmtNoleggio.setDouble(4, macchina.getPrezzo());
                stmtNoleggio.executeUpdate();

                stmtSaldo.setDouble(1, macchina.getPrezzo());
                stmtSaldo.setInt(2, utente.getIdUser());
                stmtSaldo.executeUpdate();

                stmtNascondi.setInt(1, macchina.getId());
                stmtNascondi.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new GenericSystemException("Errore durante la transazione di noleggio", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore DB: operazione di noleggio fallita", e);
        }
    }

    @Override
    public void sbloccaAutoScadute(String motivo) {
        // Se il motivo è 'Chiusura Anticipata', aggiorna data_fine a CURRENT_DATE oltre a chiudere lo stato
        String queryChiudi = "UPDATE noleggi SET " +
                "stato = 'TERMINATO', " +
                "motivo_chiusura = ?, " +
                "data_fine = CASE WHEN ? = 'Chiusura Anticipata' THEN CURRENT_DATE ELSE data_fine END " +
                "WHERE stato = 'ATTIVO' AND (data_fine < CURRENT_DATE OR ? = 'Chiusura Anticipata')";

        String queryLibera = "UPDATE macchine SET disponibile = true WHERE auto_id IN " +
                "(SELECT auto_id FROM noleggi WHERE stato = 'TERMINATO')";

        Connection conn = ConnectionHandler.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement psChiudi = conn.prepareStatement(queryChiudi);
                 PreparedStatement psLibera = conn.prepareStatement(queryLibera)) {

                psChiudi.setString(1, motivo);
                psChiudi.setString(2, motivo);
                psChiudi.setString(3, motivo);
                psChiudi.executeUpdate();

                psLibera.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new GenericSystemException("Errore durante la chiusura del noleggio: " + motivo, e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore di connessione durante lo sblocco auto", e);
        }
    }

    @Override
    public List<Macchina> getUserCars(Utente utente) {
        List<Macchina> autoNoleggiate = new ArrayList<>();
        String query = "SELECT m.* FROM macchine m JOIN noleggi n ON m.auto_id = n.auto_id WHERE n.id_utente = ? AND n.stato = 'ATTIVO'";
        Connection conn = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, utente.getIdUser());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Macchina m = new Macchina();
                    m.setId(rs.getInt(COL_AUTO_ID));
                    m.setMarca(rs.getString(COL_MARCA));
                    m.setModello(rs.getString(COL_MODELLO));
                    m.setPrezzo(rs.getInt("prezzo"));
                    m.setImageUrl(rs.getString(COL_IMMAGINE_URL));
                    autoNoleggiate.add(m);
                }
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore nel recupero delle auto attive", e);
        }
        return autoNoleggiate;
    }

    @Override
    public boolean checkInfo(Utente utente, Macchina macchina) {
        String queryUtente = "SELECT saldo FROM utenti WHERE id = ?";
        Connection con = ConnectionHandler.getInstance().getConnection();
        try (PreparedStatement psUtente = con.prepareStatement(queryUtente)) {
            psUtente.setInt(1, utente.getIdUser());
            try (ResultSet rs = psUtente.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo") >= macchina.getPrezzo();
                }
            }
            return false;
        } catch (SQLException e) {
            throw new GenericSystemException("Errore controllo saldo", e);
        }
    }

    @Override
    public List<NoleggioAuto> getRented() {
        List<NoleggioAuto> rentals = new ArrayList<>();
        String sql = "SELECT n.*, m.marca, m.modello, m.immagine_url, u.username, u.nome, u.cognome " +
                "FROM noleggi n JOIN macchine m ON n.auto_id = m.auto_id JOIN utenti u ON n.id_utente = u.id";

        Connection conn = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapResultSetToNoleggio(rs));
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore nel recupero dello storico noleggi dal database", e);
        }
        return rentals;
    }

    @Override
    public Map<LocalDate, Double> getProfittiPerData() {
        Map<LocalDate, Double> profitti = new TreeMap<>();
        String sql = "SELECT data_fine, SUM(prezzo_totale) as totale FROM noleggi WHERE stato = 'TERMINATO' GROUP BY data_fine ORDER BY data_fine ASC";
        Connection conn = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date dataSql = rs.getDate(COL_DATA_FINE);
                if (dataSql != null) {
                    profitti.put(dataSql.toLocalDate(), rs.getDouble("totale"));
                }
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore nel recupero dei profitti per il grafico", e);
        }
        return profitti;
    }

    private NoleggioAuto mapResultSetToNoleggio(ResultSet rs) throws SQLException {
        NoleggioAuto n = new NoleggioAuto();

        Macchina m = new Macchina();
        m.setId(rs.getInt(COL_AUTO_ID));
        m.setMarca(rs.getString(COL_MARCA));
        m.setModello(rs.getString(COL_MODELLO));
        m.setImageUrl(rs.getString(COL_IMMAGINE_URL));
        n.setMacchina(m);

        Utente u = new Utente();
        u.setIdUser(rs.getInt("id_utente"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setUsername(rs.getString("username"));
        n.setUtente(u);

        n.setStato(rs.getString("stato"));
        n.setPrezzoTotalePagato(rs.getDouble("prezzo_totale"));
        n.setMotivoChiusura(rs.getString("motivo_chiusura"));

        Date dataSql = rs.getDate(COL_DATA_FINE);
        if (dataSql != null) {
            n.setDataFine(dataSql.toLocalDate());
        }

        return n;
    }
}