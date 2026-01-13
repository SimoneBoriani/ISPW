package model.macchina.dao;
import bean.AggiungiAutoBean;
import bean.CatalogoBean;
import model.daoFactory.DAOFACTORY;
import model.macchina.Macchina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoMacchine extends DAOFACTORY {

    public static List<Macchina> getCars() {

        List<Macchina> cars = new ArrayList<>();
        String sql = "select * from macchine";

        try (Connection session = DriverManager.getConnection(url, user, password);
             Statement statement = session.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {

                int idAuto = rs.getInt("auto_id");
                int anno = rs.getInt("anno");
                int km = rs.getInt("km");
                String alimentazione = rs.getString("alimentazione");
                int posti = rs.getInt("posti");
                int proprietari = rs.getInt("proprietari");
                String modello = rs.getString("modello");
                String casa = rs.getString("casa");
                int prezzo = rs.getInt("prezzo");
                String tipologia = rs.getString("tipologia");

                Macchina macchina = new Macchina(idAuto, anno, km, posti, proprietari, modello, casa, alimentazione, prezzo,tipologia);
                cars.add(macchina);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cars;
    }

    public static Macchina insert(AggiungiAutoBean aggiungiautoBean) throws SQLException {

        String sql = "INSERT INTO macchine (anno, km, alimentazione, posti, proprietari, casa, modello, prezzo, tipologia) VALUES (?,?,?,?,?,?,?,?,?)";
        int generatedId = -1;

        try (Connection session = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, aggiungiautoBean.getCarYear());
            statement.setInt(2, aggiungiautoBean.getCarKm());
            statement.setString(3, aggiungiautoBean.getCarAlimentation());
            statement.setInt(4, aggiungiautoBean.getCarSeat());
            statement.setInt(5, aggiungiautoBean.getCarOwners());
            statement.setString(6, aggiungiautoBean.getCarBrand());
            statement.setString(7, aggiungiautoBean.getCarName());
            statement.setInt(8, aggiungiautoBean.getCarPrice());
            statement.setString(9, aggiungiautoBean.getCarType());

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        }


        Macchina macchina = new Macchina(
                generatedId,
                aggiungiautoBean.getCarYear(),
                aggiungiautoBean.getCarKm(),
                aggiungiautoBean.getCarSeat(),
                aggiungiautoBean.getCarOwners(),
                aggiungiautoBean.getCarName(),
                aggiungiautoBean.getCarBrand(),
                aggiungiautoBean.getCarAlimentation(),
                aggiungiautoBean.getCarPrice(),
                aggiungiautoBean.getCarType()
        );
        DaoMacchine.getCars().add(macchina);
        return macchina;
    }
    public static List<Macchina> research(CatalogoBean catalogobean) throws SQLException {
        List<Macchina> results = new ArrayList<>();

        String sql = "SELECT * FROM macchine WHERE modello ILIKE ? " +
                "AND casa ILIKE ? " +
                "AND alimentazione ILIKE ? " +
                "AND prezzo <= ? " +
                "AND km <= ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("andyfrocio:modello:"+catalogobean.getModello()+"pix:"+catalogobean.getMarca());
            ps.setString(1, "%" + (catalogobean.getModello() == null ? "" : catalogobean.getModello()) + "%");
            ps.setString(2, "%" + (catalogobean.getMarca() == null ? "" : catalogobean.getMarca()) + "%");
            ps.setString(3, "%" + (catalogobean.getAlimentazione() == null ? "" : catalogobean.getAlimentazione()) + "%");

            int p = catalogobean.getPrezzo();
            ps.setInt(4, p <= 0 ? Integer.MAX_VALUE : p);

            int k = catalogobean.getKm();
            ps.setInt(5, k <= 0 ? Integer.MAX_VALUE : k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new Macchina(
                            rs.getInt("auto_id"), rs.getInt("anno"), rs.getInt("km"),
                            rs.getInt("posti"), rs.getInt("proprietari"), rs.getString("modello"),
                            rs.getString("casa"), rs.getString("alimentazione"),
                            rs.getInt("prezzo"), rs.getString("tipologia")
                    ));
                }
            }
        }
        return results;
    }}
