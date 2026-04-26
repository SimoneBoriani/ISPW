package model.acquistoauto;

import model.macchina.Macchina;
import model.utente.Utente;

public class AcquistoAuto {

    private Macchina macchina;
    private Utente utente;

    public AcquistoAuto(){
        //Costruttore
    }

    public Macchina getMacchina(){return macchina;}
    public void setMacchina(Macchina macchina){this.macchina = macchina;}

    public Utente getUtente(){return utente;}
    public void setUtente(Utente utente){this.utente = utente;}
}