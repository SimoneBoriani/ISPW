package model.macchina.dao;

import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import model.macchina.Macchina;
import utils.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbmsDaoMacchina extends DaoMacchina {

    private static final String AUTO_ID = "auto_id";
    private static final String MODELLO = "modello";
    private static final String MARCA = "marca";
    private static final String POSTI = "posti";
    private static final String ALIMENTAZIONE = "alimentazione";
    private static final String TRASMISSIONE = "trasmissione";
    private static final String PREZZO = "prezzo";
    private static final String TIPOLOGIA = "tipologia";
    private static final String ANNO = "anno";
    private static final String IMMAGINE_URL = "immagine_url";

    private static final String ERR_ROLLBACK = "Errore critico durante il rollback: ";
    private static final String SELECT_BASE = "SELECT * FROM macchine WHERE 1=1 AND disponibile = true";

    @Override
    public void remove(int idAuto) throws GenericSystemException {
        String query = "DELETE FROM macchine WHERE auto_id = ?";
        Connection connection = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idAuto);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GenericSystemException(e.getMessage(), e);
        }
    }

    @Override
    public void insert(List<Macchina> autoDaSalvare) {
        String sql = "INSERT INTO macchine (marca, modello, tipologia, anno, prezzo, posti, alimentazione, trasmissione, immagine_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = ConnectionHandler.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (Macchina auto : autoDaSalvare) {
                    ps.setString(1, auto.getMarca());
                    ps.setString(2, auto.getModello());
                    ps.setString(3, auto.getTipologia());
                    ps.setInt(4, auto.getAnno());
                    ps.setDouble(5, auto.getPrezzo());
                    ps.setInt(6, auto.getPosti());
                    ps.setString(7, auto.getAlimentazione());
                    ps.setString(8, auto.getTrasmissione());
                    ps.setString(9, auto.getImageUrl());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                throw new GenericSystemException(ERR_ROLLBACK, ex);
            }
            throw new GenericSystemException("Errore durante il salvataggio in blocco delle auto: ", e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new GenericSystemException("Errore nel ripristino dell'autocommit: ", e);
            }
        }
    }

    @Override
    public List<Macchina> getCars() {
        List<Macchina> cars = new ArrayList<>();
        String sql = "SELECT *" + " FROM macchine WHERE disponibile = true";
        Connection session = ConnectionHandler.getInstance().getConnection();

        try (Statement statement = session.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(mapResultSetToMacchina(rs));
            }
        } catch (SQLException e) {
            throw new GenericSystemException(e.getMessage(), e);
        }
        return cars;
    }

    @Override
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {
        List<Macchina> results = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE);

        if (filtriAuto.getId() > 0) {
            sql.append(" AND auto_id = ?");
            parameters.add(filtriAuto.getId());
        } else {
            appendFilter(sql, MODELLO, filtriAuto.getModello(), parameters, true);
            appendFilter(sql, MARCA, filtriAuto.getMarca(), parameters, true);
            appendFilter(sql, ALIMENTAZIONE, filtriAuto.getAlimentazione(), parameters, true);

            if (filtriAuto.getPrezzo() > 0) {
                sql.append(" AND prezzo <= ?");
                parameters.add(filtriAuto.getPrezzo());
            }
        }

        try {
            Connection connection = ConnectionHandler.getInstance().getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                for (int i = 0; i < parameters.size(); i++) {
                    statement.setObject(i + 1, parameters.get(i));
                }

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        results.add(mapResultSetToMacchina(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore DB durante la ricerca: " + e.getMessage(), e);
        }

        if (results.isEmpty()) {
            throw new CarNotFoundException("Nessuna auto trovata con i filtri inseriti.");
        }

        return results;
    }

    private void appendFilter(StringBuilder sql, String columnName, String value, List<Object> parameters, boolean useLike) {
        if (value != null && !value.trim().isEmpty()) {
            if (useLike) {
                sql.append(String.format(" AND %s ILIKE ?", columnName));
                parameters.add("%" + value.trim() + "%");
            } else {
                sql.append(String.format(" AND %s = ?", columnName));
                parameters.add(value.trim());
            }
        }
    }

    private Macchina mapResultSetToMacchina(ResultSet rs) throws SQLException {
        return new Macchina(
                rs.getInt(AUTO_ID),
                rs.getString(MODELLO),
                rs.getString(MARCA),
                rs.getInt(POSTI),
                rs.getString(ALIMENTAZIONE),
                rs.getString(TRASMISSIONE),
                rs.getInt(PREZZO),
                rs.getString(TIPOLOGIA),
                rs.getInt(ANNO),
                rs.getString(IMMAGINE_URL)
        );
    }

    private String generateUpdateQuery(Macchina macchina, List<String> setClauses, List<Object> parameters) {
        if (macchina.getModello() != null && !macchina.getModello().trim().isEmpty()) {
            setClauses.add("modello=?");
            parameters.add(macchina.getModello());
        }

        if (macchina.getMarca() != null && !macchina.getMarca().trim().isEmpty()) {
            setClauses.add("marca=?");
            parameters.add(macchina.getMarca());
        }

        if (macchina.getAlimentazione() != null && !macchina.getAlimentazione().trim().isEmpty()) {
            setClauses.add("alimentazione=?");
            parameters.add(macchina.getAlimentazione());
        }

        if (macchina.getTrasmissione() != null && !macchina.getTrasmissione().trim().isEmpty()) {
            setClauses.add("trasmissione=?");
            parameters.add(macchina.getTrasmissione());
        }

        if (macchina.getPrezzo() > 0) {
            setClauses.add("prezzo=?");
            parameters.add(macchina.getPrezzo());
        }

        if (setClauses.isEmpty()) {
            return "";
        }

        StringBuilder query = new StringBuilder("UPDATE macchine SET ");
        query.append(String.join(", ", setClauses));
        query.append(" WHERE auto_id=?");

        parameters.add(macchina.getId());

        return query.toString();
    }

    @Override
    public void update(Macchina macchina) {
        List<String> setClauses = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        String queryStr = generateUpdateQuery(macchina, setClauses, parameters);

        if (queryStr.isEmpty()) {
            return;
        }

        Connection conn = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(queryStr)) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new GenericSystemException("Errore durante l'aggiornamento dell'auto: ", e);
        }
    }
}