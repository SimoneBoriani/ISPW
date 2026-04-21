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
    public List<Macchina> getCars() {

        List<Macchina> cars = new ArrayList<>();
        String sql = "SELECT *"+ "FROM macchine";

        Connection session = ConnectionHandler.getInstance().getConnection();

        try (Statement statement = session.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("auto_id");
                int anno = rs.getInt("anno");
                int km = rs.getInt("km");
                String alimentazione = rs.getString("alimentazione");
                int posti = rs.getInt("posti");
                int proprietari = rs.getInt("proprietari");
                String modello = rs.getString("modello");
                String casa = rs.getString("casa");
                int prezzo = rs.getInt("prezzo");
                String tipologia = rs.getString("tipologia");
                String foto=rs.getString("immagine_url");

                Macchina macchina = new Macchina(id, anno, km, posti, proprietari, modello, casa, alimentazione, prezzo, tipologia,foto);
                cars.add(macchina);
            }
        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
        return cars;
    }

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
    public void insert(Macchina macchina) throws GenericSystemException {
        String query = "INSERT INTO macchine (anno, km, alimentazione, posti, proprietari, casa, modello, prezzo, tipologia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = ConnectionHandler.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, macchina.getAnno());
            statement.setInt(2, macchina.getKm());
            statement.setString(3, macchina.getAlimentazione());
            statement.setInt(4, macchina.getPosti());
            statement.setInt(5, macchina.getProprietari());
            statement.setString(6, macchina.getCasa());
            statement.setString(7, macchina.getModello());
            statement.setInt(8, macchina.getPrezzo());
            statement.setString(9, macchina.getTipologia());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GenericSystemException(e.getMessage());
        }
    }
    @Override
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {

        List<Macchina> results = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        String brand = filtriAuto.getCasa();
        String model = filtriAuto.getModello();
        String alimentation = filtriAuto.getAlimentazione();
        int kmMax = filtriAuto.getKm();

        StringBuilder sql = new StringBuilder("SELECT * FROM macchine WHERE 1=1");

        if (model != null && !model.trim().isEmpty()) {
            sql.append(" AND modello ILIKE ?");
            parameters.add("%" + model + "%");
        }

        if (brand != null && !brand.trim().isEmpty()) {
            sql.append(" AND casa ILIKE ?");
            parameters.add("%" + brand + "%");
        }

        if (alimentation != null && !alimentation.trim().isEmpty()) {
            sql.append(" AND alimentazione ILIKE ?");
            parameters.add("%" + alimentation + "%");
        }

        if (kmMax > 0) {
            sql.append(" AND km <= ?");
            parameters.add(kmMax);
        }

        try {
            Connection connection = ConnectionHandler.getInstance().getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                for (int i = 0; i < parameters.size(); i++) {
                    statement.setObject(i + 1, parameters.get(i));
                }

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {

                        Macchina m = new Macchina(
                                rs.getInt("anno"),
                                rs.getInt("km"),
                                rs.getInt("posti"),
                                rs.getInt("proprietari"),
                                rs.getString("modello"),
                                rs.getString("casa"),
                                rs.getString("alimentazione"),
                                rs.getInt("prezzo"),
                                rs.getString("tipologia")
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

    @Override
    public void discount(int idAuto, int sconto, int prezzo) throws GenericSystemException {

        String query="UPDATE macchine SET prezzo = ? WHERE auto_id = ?";
        Connection connection = ConnectionHandler.getInstance().getConnection();
        int prezzoScontato=prezzo - (prezzo * sconto / 100);

        try(PreparedStatement statement= connection.prepareStatement(query)) {

            statement.setInt(1, prezzoScontato);
            statement.setInt(2, idAuto);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
    }
    @Override
    public void update(Macchina macchina) {
        String query = "UPDATE macchine SET anno=?, km=?, posti=?, proprietari=?, modello=?, " +
                "casa=?, alimentazione=?, prezzo=?, tipologia=?, immagine_url=? WHERE auto_id=?";

        try (Connection conn = ConnectionHandler.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, macchina.getAnno());
            ps.setInt(2, macchina.getKm());
            ps.setInt(3, macchina.getPosti());
            ps.setInt(4, macchina.getProprietari());
            ps.setString(5, macchina.getModello());
            ps.setString(6, macchina.getCasa());
            ps.setString(7, macchina.getAlimentazione());
            ps.setInt(8, macchina.getPrezzo());
            ps.setString(9, macchina.getTipologia());
            ps.setString(10, macchina.getImageUrl());

            ps.setInt(11, macchina.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new GenericSystemException(e);
        }
    }
}