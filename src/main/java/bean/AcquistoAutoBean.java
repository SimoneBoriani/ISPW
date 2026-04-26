package bean;

import model.macchina.Macchina;
import model.utente.Utente;

public class AcquistoAutoBean {

    public  Macchina macchina;
    public  Utente buyer;

    public AcquistoAutoBean(){
        //Costruttore
    }

    public Macchina getMacchina() {return macchina;}
    public void setMacchina(Macchina macchina) {this.macchina = macchina;}

    public void setBuyer(Utente user) {this.buyer = user;}
    public Utente getBuyer() {return buyer;}

}
