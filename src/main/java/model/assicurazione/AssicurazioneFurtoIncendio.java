package model.assicurazione;

public class AssicurazioneFurtoIncendio implements  Assicurazione{
    @Override
    public double calcolaCosto(double prezzoAuto) {
        return prezzoAuto * 0.1;
    }

    @Override
    public String getDescrizione() {
        return "Furto e Incendio - Include RCA e copertura danni da furto e incendio. " + "\nCosto del 10% aggiunto sul prezzo della macchina";
    }
}
