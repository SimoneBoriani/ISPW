package bean;

public class CatalogoBean {

    private int id;
    private int anno;
    private int km;
    private int posti;
    private int proprietari;
    private String modello;
    private String marca;
    private String alimentazione;
    private int prezzo;
    private String tipologia;
    private String foto;

    public CatalogoBean(){
    //Costruttore
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

    public int getId(){return id;}
    public void setId(int id){this.id=id;}

    public int getAnno(){return anno;}
    public void setAnno(int anno){this.anno=anno;}

    public int getPosti(){return posti;}
    public void setPosti(int posti){this.posti=posti;}

    public int getProprietari(){return proprietari;}
    public void setProprietari(int proprietari){this.proprietari=proprietari;}

    public String getTipologia(){return tipologia;}
    public void setTipologia(String tipologia){this.tipologia=tipologia;}

    public String getFoto(){return foto;}
    public void setFoto(String foto){this.foto=foto;}

}
