package model.duratacontrattuale;

public class NoleggioBreveTermine implements PianoNoleggio {

    @Override
    public double calcolaPrezzo(double prezzoGiornaliero, int giorni) {
        return prezzoGiornaliero * giorni;
    }

    @Override
    public String getDescrizione() {
        return "Noleggio a breve termine\n1-30 giorni\n(Tariffa base)";
    }
}