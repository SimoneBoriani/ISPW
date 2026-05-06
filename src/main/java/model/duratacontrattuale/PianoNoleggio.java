package model.duratacontrattuale;

public interface PianoNoleggio {

    double calcolaPrezzo(double prezzoGiornaliero, int giorni);

    String getDescrizione();

}
