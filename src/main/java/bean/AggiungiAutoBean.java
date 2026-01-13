package bean;

import model.macchina.Macchina;
import java.sql.SQLException;

public class AggiungiAutoBean {

    private int carYear;
    private String carName;
    private int carPrice;
    private String carType;
    private String carAlimentation;
    private String carBrand;
    private int carSeat;
    private int carOwners;
    private int carKm;

    public void setCarName(String carName) {
        this.carName = carName;
    }
    public String getCarName() {
        return carName;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
    public String getCarBrand() {
        return carBrand;
    }

    public void setCarPrice(int carPrice) {
        this.carPrice = carPrice;
    }
    public int getCarPrice() {
        return carPrice;
    }

    public void setCarAlimentation(String carAlimentation) {
        this.carAlimentation = carAlimentation;
    }
    public String getCarAlimentation() {
        return carAlimentation;
    }

    public void setCarOwners(int carOwners) {
        this.carOwners = carOwners;
    }
    public int getCarOwners() {
        return carOwners;
    }

    public void setCarKm(int carKm) {
        this.carKm = carKm;
    }
    public int getCarKm() {
        return carKm;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }
    public int getCarYear() {
        return carYear;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }
    public String getCarType() {
        return carType;
    }

    public void setCarSeat(int carSeat) {
        this.carSeat = carSeat;
    }
    public int getCarSeat() {
        return carSeat;
    }

    public Macchina sendInfo() {
        try {

            Macchina nuovaAuto = model.macchina.dao.DaoMacchine.insert(this);
            System.out.println("Salvataggio completato con successo nel database!");
            return nuovaAuto;
        } catch (SQLException e) {
            System.err.println("Errore durante il salvataggio dell'auto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}