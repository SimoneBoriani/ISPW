package model.noleggio;

import model.duratacontrattuale.PianoNoleggio;

public class NoleggioBreveTermine implements PianoNoleggio {

    @Override
    public double calcolaPrezzo(double prezzoGiornaliero, int giorni) {
        return prezzoGiornaliero * giorni;
    }

    @Override
    public String getDescrizione() {
        return "Noleggio a breve - 1-30 giorni -termine (Tariffa base)";
    }
}