package bean;

import model.macchina.Macchina;
import model.utente.Utente;

public class SegnalazioneBean {

    private int id;
    private Utente utente;
    private Macchina macchina;
    private String msg;

    public SegnalazioneBean(){
        //Costruttore
    }

    public void setId(int id) {this.id = id;}
    public int getId() {return id;}

    public void setUtente(Utente utente){this.utente=utente;}
    public Utente getUtente(){return this.utente;}

    public void setMsg(String msg){this.msg=msg;}
    public  String getMsg(){return this.msg;}

    public void setMacchina(Macchina macchina){this.macchina=macchina;}
    public Macchina getMacchina(){return this.macchina;}

}