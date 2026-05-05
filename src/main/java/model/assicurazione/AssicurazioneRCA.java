package model.assicurazione;

public class AssicurazioneRCA implements  Assicurazione {

    @Override
    public double calcolaCosto(double prezzoAuto) {
        return prezzoAuto*0.02;
    }

    @Override
    public String getDescrizione() {
        return "RCA Base - Copertura obbligatoria minima. " + "\nCosto del 2% aggiunto sul prezzo della macchina";
    }
}
