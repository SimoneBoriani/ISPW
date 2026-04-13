package bean;

import model.macchina.Macchina;
import model.macchina.dao.DaoMacchina;
import model.macchina.dao.DbmsDaoMacchina;

import java.sql.SQLException;
import java.util.logging.Logger;

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

    public AggiungiAutoBean() {
    }

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

}