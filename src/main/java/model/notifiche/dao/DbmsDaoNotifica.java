package model.notifiche.dao;

import exceptions.GenericSystemException;
import model.notifiche.Notifica;
import utils.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbmsDaoNotifica extends DaoNotifica {

    private static final Logger logger = Logger.getLogger(DbmsDaoNotifica.class.getName());

    @Override
    public void inserisci(Notifica notifica) {
        String query = "INSERT INTO notifiche (mittente, destinatario, testo, tipo, letta, data_creazione, auto) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionHandler.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, notifica.getMittente());
            pstmt.setString(2, notifica.getDestinatario());
            pstmt.setString(3, notifica.getTesto());
            pstmt.setString(4, notifica.getTipo().name());
            pstmt.setBoolean(5, notifica.isLetta());

            LocalDateTime data = notifica.getDataCreazione() != null ? notifica.getDataCreazione() : LocalDateTime.now();
            pstmt.setTimestamp(6, Timestamp.valueOf(data));

            pstmt.setString(7, notifica.getAuto());

            pstmt.executeUpdate();

        } catch (SQLException | GenericSystemException e) {
            logger.log(Level.SEVERE, "Errore durante l'inserimento della notifica nel DB", e);
        }
    }

    @Override
    public List<Notifica> getComunicazioniPerDestinatario(String destinatario) {
        String query = "SELECT * " + "FROM notifiche WHERE destinatario = ? ORDER BY data_creazione DESC";
        return eseguiQuerySelect(query, destinatario);
    }

    @Override
    public List<Notifica> getNonLette(String destinatario) {
        String query = "SELECT * " + "FROM notifiche WHERE destinatario = ? AND letta = false ORDER BY data_creazione DESC";
        return eseguiQuerySelect(query, destinatario);
    }

    @Override
    public void segnaComeLetta(int id) {
        String query = "UPDATE notifiche SET letta = true WHERE id = ?";

        try (Connection conn = ConnectionHandler.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException | GenericSystemException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'aggiornamento dello stato 'letta' (ID: " + id + ")");
        }
    }

    private List<Notifica> eseguiQuerySelect(String query, String parametro) {
        List<Notifica> lista = new ArrayList<>();

        try (Connection conn = ConnectionHandler.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notifica n = new Notifica(
                            rs.getInt("id"),
                            rs.getString("mittente"),
                            rs.getString("destinatario"),
                            rs.getString("testo"),
                            Notifica.Tipo.valueOf(rs.getString("tipo")),
                            rs.getBoolean("letta"),
                            rs.getTimestamp("data_creazione").toLocalDateTime()
                    );
                    n.setAuto(rs.getString("auto"));

                    lista.add(n);
                }
            }

        } catch (SQLException | GenericSystemException e) {
            logger.log(Level.SEVERE, "Errore durante l'estrazione delle notifiche", e);
        }
        return lista;
    }

    public void eliminaNotifica(int idNotifica) {
        String query = "DELETE FROM notifiche WHERE id = ?";

        Connection conn = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idNotifica);
            pstmt.executeUpdate();


        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'eliminazione notifiche", e);
        }
    }
}