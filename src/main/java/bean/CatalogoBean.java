package bean;

public class CatalogoBean {

    private String modello="";
    private String marca="";
    private int prezzo=0;
    private int km=0;
    private String alimentazione="";

    public CatalogoBean(){

    }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public int getPrezzo() { return prezzo; }
    public void setPrezzo(int prezzo) { this.prezzo = prezzo; }

    public int getKm() { return km; }
    public void setKm(int km) { this.km = km; }

    public String getAlimentazione() { return alimentazione; }
    public void setAlimentazione(String alimentazione) { this.alimentazione = alimentazione; }

}
