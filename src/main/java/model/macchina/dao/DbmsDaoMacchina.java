package model.macchina.dao;

import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import model.macchina.Macchina;
import utils.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbmsDaoMacchina extends DaoMacchina {

    @Override
    public void remove(int idAuto) throws  GenericSystemException {

        String query = "DELETE FROM macchine WHERE auto_id = ?";
        Connection connection = ConnectionHandler.getInstance().getConnection();

        try(PreparedStatement statement = connection.prepareStatement(query)){

            statement.setInt(1, idAuto);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new GenericSystemException(e.getMessage());
        }
    }

    @Override
    public void insert(List<Macchina> autoDaSalvare) {

        String sql = "INSERT INTO macchine (marca, modello, tipologia, anno, prezzo, posti, alimentazione, trasmissione, immagine_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = ConnectionHandler.getInstance().getConnection();

        try {

            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {

                for (Macchina auto : autoDaSalvare) {
                    ps.setString(1, auto.getMarca());
                    ps.setString(2, auto.getModello());
                    ps.setString(3, auto.getTipologia());
                    ps.setInt(4, auto.getAnno());
                    ps.setInt(5, auto.getPrezzo());
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
                throw new GenericSystemException("Errore critico durante il rollback: ", ex);
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
                Macchina macchina = new Macchina(
                        rs.getInt("auto_id"),
                        rs.getString("modello"),
                        rs.getString("marca"),
                        rs.getInt("posti"),
                        rs.getString("alimentazione"),
                        rs.getString("trasmissione"),
                        rs.getInt("prezzo"),
                        rs.getString("tipologia"),
                        rs.getInt("anno"),
                        rs.getString("immagine_url")
                );
                cars.add(macchina);
            }
        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
        return cars;
    }

    @Override
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {

        List<Macchina> results = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        String brand = filtriAuto.getMarca();
        String model = filtriAuto.getModello();
        String alimentation = filtriAuto.getAlimentazione();
        int prezzoMax = filtriAuto.getPrezzo();

        // FIX: Aggiunto "disponibile = true" in modo che la ricerca non mostri auto già noleggiate
        StringBuilder sql = new StringBuilder("SELECT * FROM macchine WHERE 1=1 AND disponibile = true");

        if (model != null && !model.trim().isEmpty()) {
            sql.append(" AND modello ILIKE ?");
            parameters.add("%" + model + "%");
        }

        if (brand != null && !brand.trim().isEmpty()) {
            sql.append(" AND marca ILIKE ?"); // FIX: cambiato da casa a marca
            parameters.add("%" + brand + "%");
        }

        if (alimentation != null && !alimentation.trim().isEmpty()) {
            sql.append(" AND alimentazione ILIKE ?");
            parameters.add("%" + alimentation + "%");
        }

        if (prezzoMax > 0){
            sql.append(" AND prezzo <= ?");
            parameters.add(prezzoMax);
        }

        try {
            Connection connection = ConnectionHandler.getInstance().getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                for (int i = 0; i < parameters.size(); i++) {
                    statement.setObject(i + 1, parameters.get(i));
                }

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {

                        // FIX: Adesso utilizza il nuovo costruttore completo per il noleggio!
                        Macchina m = new Macchina(
                                rs.getInt("auto_id"),
                                rs.getString("modello"),
                                rs.getString("marca"),
                                rs.getInt("posti"),
                                rs.getString("alimentazione"),
                                rs.getString("trasmissione"),
                                rs.getInt("prezzo"),
                                rs.getString("tipologia"),
                                rs.getInt("anno"),
                                rs.getString("immagine_url")
                        );

                        results.add(m);
                    }
                }
            }
        } catch (SQLException e) {
            throw new GenericSystemException("Errore DB durante la ricerca: " + e.getMessage());
        }

        if (results.isEmpty()) {
            throw new CarNotFoundException("Nessuna auto trovata con i filtri inseriti.");
        }

        return results;
    }


    private String generateUpdateQuery(Macchina macchina, List<String> setClauses, List<Object> parameters) {

        // FIX: Rimosso macchina.getKm() che non esiste più

        if (macchina.getModello() != null && !macchina.getModello().trim().isEmpty()) {
            setClauses.add("modello=?");
            parameters.add(macchina.getModello());
        }

        // FIX: Cambiato da getCasa() a getMarca()
        if (macchina.getMarca() != null && !macchina.getMarca().trim().isEmpty()) {
            setClauses.add("marca=?");
            parameters.add(macchina.getMarca());
        }

        if (macchina.getAlimentazione() != null && !macchina.getAlimentazione().trim().isEmpty()) {
            setClauses.add("alimentazione=?");
            parameters.add(macchina.getAlimentazione());
        }

        // FIX: Aggiunta la Trasmissione
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