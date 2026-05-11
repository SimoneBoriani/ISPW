package model.noleggioauto;

import model.duratacontrattuale.PianoNoleggio;
import model.macchina.Macchina;
import model.utente.Utente;
import java.time.LocalDate;

public class NoleggioAuto {

    private Macchina macchina;
    private Utente utente;
    private PianoNoleggio pianoScelto;
    private int giorniNoleggio;
    private String stato;
    private LocalDate dataFine;
    private double prezzoTotalePagato;
    private String motivoChiusura;

    public NoleggioAuto() {
        // Costruttore
    }

    public Macchina getMacchina() { return macchina; }
    public void setMacchina(Macchina macchina) { this.macchina = macchina; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public PianoNoleggio getPianoScelto() { return pianoScelto; }
    public void setPianoScelto(PianoNoleggio pianoScelto) { this.pianoScelto = pianoScelto; }

    public int getGiorniNoleggio() { return giorniNoleggio; }
    public void setGiorniNoleggio(int giorniNoleggio) { this.giorniNoleggio = giorniNoleggio; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }

    public double getPrezzoTotalePagato() { return prezzoTotalePagato; }
    public void setPrezzoTotalePagato(double prezzoTotalePagato) { this.prezzoTotalePagato = prezzoTotalePagato; }

    public String getMotivoChiusura() { return motivoChiusura; }
    public void setMotivoChiusura(String motivoChiusura) { this.motivoChiusura = motivoChiusura; }
}