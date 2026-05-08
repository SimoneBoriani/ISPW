package bean;

import model.macchina.Macchina;
import model.utente.Utente;

public class NoleggioAutoBean {
    public Macchina macchina;
    public Utente renter;
    public int giorni;

    public NoleggioAutoBean() {
        //Costruttore
    }

    public Macchina getMacchina() { return macchina; }
    public void setMacchina(Macchina macchina) { this.macchina = macchina; }

    public Utente getRenter() { return renter; }
    public void setRenter(Utente renter) { this.renter = renter; }

    public int getGiorni() { return giorni; }
    public void setGiorni(int giorni) { this.giorni = giorni; }
}