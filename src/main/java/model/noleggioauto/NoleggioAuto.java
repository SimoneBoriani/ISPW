package model.noleggioauto;

import model.duratacontrattuale.PianoNoleggio;
import model.macchina.Macchina;
import model.utente.Utente;

public class NoleggioAuto {

    private Macchina macchina;
    private Utente utente;
    private PianoNoleggio pianoScelto;
    private int giorniNoleggio;

    public NoleggioAuto() {
        // Costruttore
    }


    public double calcolaPrezzoTotale() {

        double prezzoAuto = macchina.getPrezzo();

        double costoFinale = pianoScelto.calcolaPrezzo(prezzoAuto, giorniNoleggio);

        return costoFinale;
    }

    public Macchina getMacchina() { return macchina; }
    public void setMacchina(Macchina macchina) { this.macchina = macchina; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public PianoNoleggio getPianoScelto() { return pianoScelto; }
    public void setPianoScelto(PianoNoleggio pianoScelto) { this.pianoScelto = pianoScelto; }

    public int getGiorniNoleggio() { return giorniNoleggio; }
    public void setGiorniNoleggio(int giorniNoleggio) { this.giorniNoleggio = giorniNoleggio; }
}