package model.macchina;

public class Macchina {

    private int id_auto;
    private int anno;
    private int km;
    private int posti;
    private int sconto;
    private int proprietari;
    private String modello;
    private String casa;
    private String alimentazione;
    private int prezzo;

    public Macchina(int id_auto,int anno,int km,int posti,int sconto,int prorpietari,String nome,String casa,String alimentazione,int prezzo){
        this.id_auto=id_auto;
        this.anno=anno;
        this.km=km;
        this.posti=posti;
        this.sconto=sconto;
        this.proprietari=prorpietari;
        this.modello=nome;
        this.casa=casa;
        this.alimentazione=alimentazione;
        this.prezzo=prezzo;
    }

    public String getModello() { return modello; }
    public String getCasa() { return casa; }
    public int getKm() { return km; }
    public int getPosti() { return posti; }
    public int getProprietari() { return proprietari; }
    public int getAnno() { return anno; }
    public String getAlimentazione() { return alimentazione; }
    public int getSconto() { return sconto; }
    public int getPrezzo() { return prezzo; }
}
