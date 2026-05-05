package model.acquistoauto;

import model.assicurazione.Assicurazione;
import model.macchina.Macchina;
import model.utente.Utente;

public class AcquistoAuto {

    private Macchina macchina;
    private Utente utente;
    private Assicurazione assicurazioneScelta;

    public AcquistoAuto(){
        //Costruttore
    }

    public double calcolaPrezzoTotale() {
        double prezzoAuto = macchina.getPrezzo();

        double costoAssicurazione = assicurazioneScelta.calcolaCosto(prezzoAuto);

        return prezzoAuto + costoAssicurazione;
    }

    public Macchina getMacchina(){return macchina;}
    public void setMacchina(Macchina macchina){this.macchina = macchina;}

    public Utente getUtente(){return utente;}
    public void setUtente(Utente utente){this.utente = utente;}
}