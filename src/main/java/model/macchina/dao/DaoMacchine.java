package model.macchina.dao;
import bean.AggiungiAutoBean;
import model.DAOFACTORY.DAOFACTORY;
import model.macchina.Macchina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoMacchine extends DAOFACTORY {

    public static List<Macchina> getCars(){

        List<Macchina> cars = new ArrayList<>();
        String sql="select * from macchine";

        try(Connection session= DriverManager.getConnection(url,user,password);
        Statement statement=session.createStatement();
        ResultSet rs=statement.executeQuery(sql)) {

            while (rs.next()){

                int id_auto = rs.getInt("auto_id");
                int anno = rs.getInt("anno");
                int km = rs.getInt("km");
                String alimentazione = rs.getString("alimentazione");
                int posti = rs.getInt("posti");
                int proprietari = rs.getInt("proprietari");
                String modello = rs.getString("modello");
                String casa = rs.getString("casa");
                int prezzo = rs.getInt("prezzo");

            Macchina macchina=new Macchina(id_auto,anno,km,posti,proprietari,modello,casa,alimentazione,prezzo);
            cars.add(macchina);
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    return cars;
    }

    public static Macchina insert(AggiungiAutoBean aggiungiautoBean) throws SQLException {

        String sql = "INSERT INTO macchine (anno, km, alimentazione, posti, proprietari, casa, modello, prezzo) VALUES (?,?,?,?,?,?,?,?)";
        int generatedId = -1;

        try (Connection session = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            System.out.println(aggiungiautoBean.getCar_owners());
            statement.setInt(1, aggiungiautoBean.getCar_year());
            statement.setInt(2, aggiungiautoBean.getCar_km());
            statement.setString(3, aggiungiautoBean.getCar_alimentation());
            statement.setInt(4, aggiungiautoBean.getCar_seat());
            statement.setInt(5, aggiungiautoBean.getCar_owners());
            statement.setString(6, aggiungiautoBean.getCar_brand());
            statement.setString(7, aggiungiautoBean.getCar_name());
            statement.setInt(8, aggiungiautoBean.getCar_price());

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        }


        Macchina macchina = new Macchina(
                generatedId,
                aggiungiautoBean.getCar_year(),
                aggiungiautoBean.getCar_km(),
                aggiungiautoBean.getCar_seat(),
                aggiungiautoBean.getCar_owners(),
                aggiungiautoBean.getCar_name(),
                aggiungiautoBean.getCar_brand(),
                aggiungiautoBean.getCar_alimentation(),
                aggiungiautoBean.getCar_price()
        );
        return macchina;
    }
}
