package bean;

import model.macchina.Macchina;
import model.utente.Utente;

public class NoleggioAutoBean {

    private Macchina macchina;
    private Utente renter;
    private int giorni;
    private String metodoPagamento;
    private boolean pagamentoEsternoConfermato = false;

    public NoleggioAutoBean() {}

    public Macchina getMacchina() { return macchina; }
    public void setMacchina(Macchina macchina) { this.macchina = macchina; }

    public Utente getRenter() { return renter; }
    public void setRenter(Utente renter) { this.renter = renter; }

    public int getGiorni() { return giorni; }
    public void setGiorni(int giorni) { this.giorni = giorni; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public boolean isPagamentoEsternoConfermato() { return pagamentoEsternoConfermato; }
    public void setPagamentoEsternoConfermato(boolean pagamentoEsternoConfermato) {this.pagamentoEsternoConfermato = pagamentoEsternoConfermato;}
}