package model.assicurazione;

public class AssicurazioneKasko implements Assicurazione{

    @Override
    public double calcolaCosto(double prezzoAuto) {
        return prezzoAuto * 0.2;
    }

    @Override
    public String getDescrizione() {
        return "Kasko Completa - Copertura totale, anche per danni causati da te.";
    }
}
