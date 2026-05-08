package model.duratacontrattuale;

public class NoleggioMedioTermine implements PianoNoleggio {

    @Override
    public double calcolaPrezzo(double prezzoGiornaliero, int giorni) {

        double costoBase = prezzoGiornaliero * giorni;
        return costoBase - (costoBase * 0.05);
    }

    @Override
    public String getDescrizione() {

        return "Noleggio a medio termine\n31-364 giorni\n(Sconto 5%)";

    }
}