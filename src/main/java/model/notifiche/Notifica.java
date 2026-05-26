package model.notifiche;

import java.time.LocalDateTime;

public class Notifica{

    public enum Tipo {
        SISTEMA,
        MESSAGGIO
    }

    private int id;
    private String mittente;
    private String destinatario;
    private String auto;
    private String testo;
    private Tipo tipo;
    private boolean letta;
    private LocalDateTime dataCreazione;

    public Notifica() {
        //Costruttore
    }

    public Notifica(int id, String mittente, String destinatario, String testo,
                         Tipo tipo, boolean letta, LocalDateTime dataCreazione) {
        this.id = id;
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.testo = testo;
        this.tipo = tipo;
        this.letta = letta;
        this.dataCreazione = dataCreazione;

    }


    public Notifica(String mittente, String destinatario, String testo, Tipo tipo) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.testo = testo;
        this.tipo = tipo;
        this.letta = false;
        this.dataCreazione = LocalDateTime.now();
    }

    public int getId() {return id;}
    public void setId(int id) {
        this.id = id;
    }

    public String getMittente() {
        return mittente;
    }
    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getTesto() {
        return testo;
    }
    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Tipo getTipo() {
        return tipo;
    }
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public boolean isLetta() {
        return letta;
    }
    public void setLetta(boolean letta) {
        this.letta = letta;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }
    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String getAuto(){return auto;}
    public void setAuto(String auto){this.auto = auto;}

}