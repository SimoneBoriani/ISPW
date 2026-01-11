package bean;

public class AggiungiAutoBean {

    private int car_year;
    private String car_name;
    private int car_price;
    private String car_type;
    private String car_alimentation;
    private String car_brand;
    private int car_seat;
    private int car_owners;
    int car_km;

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }
    public String getCar_name() {
        return car_name;
    }

    public void setCar_brand(String car_brand) {this.car_brand = car_brand;}
    public String getCar_brand() {
        return car_brand;
    }

    public void setCar_price(int car_price) {this.car_price = car_price;}
    public int getCar_price() {return car_price;
    }

    public void setCar_alimentation(String car_alimentation) {
        this.car_alimentation = car_alimentation;
    }
    public String getCar_alimentation() {
        return car_alimentation;
    }

    public void setCar_owners(int car_owners) {
        this.car_owners = car_owners;
    }
    public int getCar_owners() {
        return car_owners;
    }

    public void setCar_km(int car_km) {
        this.car_km = car_km;
    }
    public int getCar_km() {
        return car_km;
    }

    public void setCar_year(int car_year) {
        this.car_year = car_year;
    }
    public int getCar_year() {
        return car_year;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }
    public String getCar_type() {
        return car_type;
    }

    public void setCar_seat(int car_seat) {
        this.car_seat = car_seat;
    }
    public int getCar_seat() {
        return car_seat;
    }

    public AggiungiAutoBean sendinfo() {
        try {
            // Chiamiamo il metodo del DAO passando questo oggetto Bean
            // DaoMacchine.insert si occuperà di aprire la connessione e fare la query
            model.macchina.dao.DaoMacchine.insert(this);

            System.out.println("Salvataggio completato con successo nel database!");
        } catch (java.sql.SQLException e) {
            System.err.println("Errore durante il salvataggio dell'auto: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
