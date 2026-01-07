package bean;
import model.macchina.Macchina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MacchineBean {


    public static List<Macchina> getCars(){

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "admin";
        String password = "password";

        List<Macchina> cars = new ArrayList<>();
        String sql="select * from macchine"; //da mettere nel dao

        /*try(Connection session= DriverManager.getConnection(url,user,password);
        Statement statement=session.createStatement();
        ResultSet rs=statement.executeQuery(sql)) {

            while (rs.next()){

                int id_auto = rs.getInt("auto_id");
                int anno = rs.getInt("anno");
                int km = rs.getInt("km");
                String alimentazione = rs.getString("alimentazione");
                int posti = rs.getInt("posti");
                int proprietari = rs.getInt("proprietari");
                int sconto = rs.getInt("sconto");
                String modello = rs.getString("modello");
                String casa = rs.getString("casa");
                int prezzo = rs.getInt("prezzo");*/

            Macchina macchina=new Macchina(id_auto,anno,km,posti,sconto,proprietari,modello,casa,alimentazione,prezzo);
            if(macchina!=null){cars.add(macchina);}
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    return cars;
    }
}
