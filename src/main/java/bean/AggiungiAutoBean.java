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
    Logger logger = Logger.getLogger(getClass().getName());

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

    public void sendInfo() {
        try {
            // 1. Creiamo l'oggetto Macchina usando i dati memorizzati in questo Bean
            // Utilizziamo il costruttore senza ID, poiché ci pensa il DB a generarlo
            Macchina nuovaMacchina = new Macchina(
                    this.carYear,
                    this.carKm,
                    this.carSeat,
                    this.carOwners,
                    this.carName,
                    this.carBrand,
                    this.carAlimentation,
                    this.carPrice,
                    this.carType
            );

            // 2. Usiamo la tua Factory per ottenere il DAO corretto e inseriamo l'auto
            model.daofactory.DaoFactory.getDaoSingletonFactory().createMacchinaDao().insert(nuovaMacchina);

        } catch (exceptions.GenericSystemException e) {
            logger.info("Errore di sistema: " + e.getMessage());
        } catch (Exception e) {
            logger.info("Errore generico: " + e.getMessage());
        }
    }
}