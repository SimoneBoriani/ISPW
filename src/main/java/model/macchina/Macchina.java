package model.macchina;

public class Macchina {

    private int idAuto;
    private int anno;
    private int km;
    private int posti;
    private int proprietari;
    private String modello;
    private String casa;
    private String alimentazione;
    private int prezzo;
    private String tipologia;

    public Macchina(int idAauto,int anno,int km,int posti,int prorpietari,String nome,String casa,String alimentazione,int prezzo,String tipologia){
        this.idAuto=idAuto;
        this.anno=anno;
        this.km=km;
        this.posti=posti;
        this.proprietari=prorpietari;
        this.modello=nome;
        this.casa=casa;
        this.alimentazione=alimentazione;
        this.prezzo=prezzo;
        this.tipologia=tipologia;
    }
    public int getIdAuto() { return idAuto; }
    public void setIdAuto(int idAuto) { this.idAuto = idAuto; }

    public int getAnno() { return anno; }
    public void setAnno(int anno) { this.anno = anno; }

    public int getKm() { return km; }
    public void setKm(int km) { this.km = km; }

    public int getPosti() { return posti; }
    public void setPosti(int posti) { this.posti = posti; }

    public int getProprietari() { return proprietari; }
    public void setProprietari(int proprietari) { this.proprietari = proprietari; }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getCasa() { return casa; }
    public void setCasa(String casa) { this.casa = casa; }

    public String getAlimentazione() { return alimentazione; }
    public void setAlimentazione(String alimentazione) { this.alimentazione = alimentazione; }

    public int getPrezzo() { return prezzo; }
    public void setPrezzo(int prezzo) { this.prezzo = prezzo; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }
}
