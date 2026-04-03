package bean;

import model.macchina.Macchina;
import model.macchina.dao.DbmsDaoMacchina;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CatalogoBean {

    Logger logger = Logger.getLogger(getClass().getName());
    private String modello="";
    private String marca="";
    private int prezzo=0;
    private int km=0;
    private String alimentazione="";

    public static List<Macchina> getCars() {
        return DbmsDaoMacchina.getCars();
    }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public int getPrezzo() { return prezzo; }
    public void setPrezzo(int prezzo) { this.prezzo = prezzo; }

    public int getKm() { return km; }
    public void setKm(int km) { this.km = km; }

    public String getAlimentazione() { return alimentazione; }
    public void setAlimentazione(String alimentazione) { this.alimentazione = alimentazione; }

    public List<Macchina> sendInfo() {

        try {
            return DbmsDaoMacchina.research(this);
        } catch (java.sql.SQLException e) {
            logger.info(e.getMessage());
            return new ArrayList<>();
        }
    }

}
