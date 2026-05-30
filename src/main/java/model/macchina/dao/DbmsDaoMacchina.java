package model.macchina.dao;

import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import model.macchina.Macchina;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
    private static final String SELECT_BASE = "SELECT * FROM macchine WHERE 1=1 AND disponibile = true";

    private static final Logger LOGGER = LogManager.getLogger(DbmsDaoMacchina.class);

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

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = ConnectionHandler.getInstance().getConnection();
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            ps = connection.prepareStatement(sql);
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
            connection.commit();
            connection.setAutoCommit(originalAutoCommit);

        } catch (SQLException e) {

            try {
                    connection.rollback();
                    LOGGER.warn("Rollback eseguito a seguito di un'eccezione: {}", e.getMessage());
            } catch (SQLException rollbackEx) {
                LOGGER.error("Errore critico durante il rollback: {}", rollbackEx.getMessage());
            }

            throw new GenericSystemException("Errore durante il salvataggio in blocco: " + e.getMessage(), e);
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException closeEx) {
                    LOGGER.error("Errore durante la chiusura del PreparedStatement: {}", closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public List<Macchina> getCars() {
        List<Macchina> listaMacchine = new ArrayList<>();
        String sql = "SELECT * " + "FROM macchine WHERE disponibile = true";

        try (Connection conn = ConnectionHandler.getInstance().getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Macchina m = mapResultSetToMacchina(rs);
                listaMacchine.add(m);
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore nel recupero delle macchine dal database", e);
        }
        return listaMacchine;
    }

    @Override
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {
        List<Macchina> results = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        if (filtriAuto != null && filtriAuto.getId() > 0) {
            sql.append("SELECT * FROM macchine WHERE auto_id = ?");
            parameters.add(filtriAuto.getId());
        }
        else {
            sql.append(SELECT_BASE);

            if (filtriAuto != null) {

                appendFilter(sql, MODELLO, filtriAuto.getModello(), parameters, true);
                appendFilter(sql, MARCA, filtriAuto.getMarca(), parameters, true);
                appendFilter(sql, ALIMENTAZIONE, filtriAuto.getAlimentazione(), parameters, true);
                appendFilter(sql, TIPOLOGIA, filtriAuto.getTipologia(), parameters, true);
                appendFilter(sql, TRASMISSIONE, filtriAuto.getTrasmissione(), parameters, true);

                if (filtriAuto.getPrezzo() > 0) {
                    sql.append(" AND prezzo <= ?");
                    parameters.add(filtriAuto.getPrezzo());
                }
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

        Macchina macchina = new Macchina();

        macchina.setId(rs.getInt(AUTO_ID));
        macchina.setModello(rs.getString(MODELLO));
        macchina.setMarca(rs.getString(MARCA));
        macchina.setPosti(rs.getInt(POSTI));
        macchina.setAlimentazione(rs.getString(ALIMENTAZIONE));
        macchina.setTrasmissione(rs.getString(TRASMISSIONE));
        macchina.setPrezzo(rs.getDouble(PREZZO));
        macchina.setTipologia(rs.getString(TIPOLOGIA));
        macchina.setAnno(rs.getInt(ANNO));
        macchina.setImageUrl(rs.getString(IMMAGINE_URL));

        return macchina;
    }

    private String generateUpdateQuery(Macchina nuova, Macchina db, List<String> setClauses, List<Object> parameters) {

        record FieldRule(String col, Function<Macchina, Object> getter, java.util.function.Predicate<Macchina> isValid) {}
        List<FieldRule> rules = List.of(
                new FieldRule(MODELLO, Macchina::getModello, m -> m.getModello() != null && !m.getModello().isBlank()),
                new FieldRule(MARCA, Macchina::getMarca, m -> m.getMarca() != null && !m.getMarca().isBlank()),
                new FieldRule(ANNO, Macchina::getAnno, m -> m.getAnno() > 0),
                new FieldRule(POSTI, Macchina::getPosti, m -> m.getPosti() > 0),
                new FieldRule(ALIMENTAZIONE, Macchina::getAlimentazione, m -> m.getAlimentazione() != null && !m.getAlimentazione().isBlank()),
                new FieldRule(TRASMISSIONE, Macchina::getTrasmissione, m -> m.getTrasmissione() != null && !m.getTrasmissione().isBlank()),
                new FieldRule(TIPOLOGIA, Macchina::getTipologia, m -> m.getTipologia() != null && !m.getTipologia().isBlank()),
                new FieldRule(PREZZO, Macchina::getPrezzo, m -> m.getPrezzo() > 0),
                new FieldRule(IMMAGINE_URL, Macchina::getImageUrl, m -> m.getImageUrl() != null && !m.getImageUrl().isBlank())
        );

        for (FieldRule rule : rules) {
            Object newVal = rule.getter().apply(nuova);
            Object oldVal = rule.getter().apply(db);

            if (rule.isValid().test(nuova) && !Objects.equals(newVal, oldVal)) {
                setClauses.add(rule.col() + "=?");
                parameters.add(newVal);
            }
        }

        if (setClauses.isEmpty()) return "";

        parameters.add(nuova.getId());
        return "UPDATE macchine SET " + String.join(", ", setClauses) + " WHERE auto_id=?";
    }

    @Override
    public void update(Macchina macchina) {

        List<String> setClauses = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        Macchina macchinaDb = research(macchina).get(0);

        if (macchinaDb == null) {
            throw new CarNotFoundException("Auto non trovata.");
        }

        String queryStr = generateUpdateQuery(
                macchina,
                macchinaDb,
                setClauses,
                parameters
        );

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

            throw new GenericSystemException(
                    "Errore durante l'aggiornamento dell'auto: ",
                    e
            );
        }
    }
}