package bean;

public class CatalogoBean {

    private int id;
    private String modello;
    private String marca;
    private int anno;
    private int posti;
    private String alimentazione;
    private String trasmissione;
    private double prezzo;
    private String tipologia;
    private String foto;

    public CatalogoBean() {
        // Costruttore vuoto
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public int getAnno() { return anno; }
    public void setAnno(int anno) { this.anno = anno; }

    public int getPosti() { return posti; }
    public void setPosti(int posti) { this.posti = posti; }

    public String getAlimentazione() { return alimentazione; }
    public void setAlimentazione(String alimentazione) { this.alimentazione = alimentazione; }

    public String getTrasmissione() { return trasmissione; }
    public void setTrasmissione(String trasmissione) { this.trasmissione = trasmissione; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
