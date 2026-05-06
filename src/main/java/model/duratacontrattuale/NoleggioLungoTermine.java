package model.duratacontrattuale;

public class NoleggioLungoTermine implements PianoNoleggio {

    @Override
    public double calcolaPrezzo(double prezzoGiornaliero, int giorni) {

        double costoBase = prezzoGiornaliero * giorni;
        return costoBase - (costoBase * 0.15);
    }

    @Override
    public String getDescrizione() {

        return "Noleggio a lungo termine- 365-700 giorni -(Sconto 15%)";

    }
}