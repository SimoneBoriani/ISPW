package model.macchina;

public class Macchina {

    private int id; // auto_id nel DB
    private String modello;
    private String marca;
    private int posti;
    private String alimentazione;
    private String trasmissione; // Nuovo campo
    private int prezzo; // Prezzo di noleggio (es. giornaliero)
    private String tipologia;
    private int anno;
    private String foto; // immagine_url nel DB

    public Macchina() {}

    public Macchina(int id, String modello, String marca, int posti, String alimentazione, String trasmissione, int prezzo, String tipologia, int anno, String foto) {
        this.id = id;
        this.modello = modello;
        this.marca = marca;
        this.posti = posti;
        this.alimentazione = alimentazione;
        this.trasmissione = trasmissione;
        this.prezzo = prezzo;
        this.tipologia = tipologia;
        this.anno = anno;
        this.foto = foto;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public int getPosti() { return posti; }
    public void setPosti(int posti) { this.posti = posti; }

    public String getAlimentazione() { return alimentazione; }
    public void setAlimentazione(String alimentazione) { this.alimentazione = alimentazione; }

    public String getTrasmissione() { return trasmissione; }
    public void setTrasmissione(String trasmissione) { this.trasmissione = trasmissione; }

    public int getPrezzo() { return prezzo; }
    public void setPrezzo(int prezzo) { this.prezzo = prezzo; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public int getAnno() { return anno; }
    public void setAnno(int anno) { this.anno = anno; }

    public String getImageUrl() { return foto; }
    public void setImageUrl(String foto) { this.foto = foto; }
}