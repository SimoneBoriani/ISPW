package model.duratacontrattuale;

public class NoleggioMedioTermine implements PianoNoleggio {

    @Override
    public double calcolaPrezzo(double prezzoGiornaliero, int giorni) {

        double costoBase = prezzoGiornaliero * giorni;
        return costoBase - (costoBase * 0.5);
    }

    @Override
    public String getDescrizione() {

        return "Noleggio a medio termine- 31-364 giorni - (Sconto 5%)";

    }
}