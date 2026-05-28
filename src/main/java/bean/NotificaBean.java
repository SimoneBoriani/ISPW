package bean;

import model.macchina.Macchina;
import model.utente.Utente;

public class NotificaBean {

    private String id;
    private Utente utente;
    private Macchina macchina;
    private String msg;

    public NotificaBean(){
        //Costruttore
    }

    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setUtente(Utente utente){this.utente=utente;}
    public Utente getUtente(){return this.utente;}

    public void setMsg(String msg){this.msg=msg;}
    public  String getMsg(){return this.msg;}

    public void setMacchina(Macchina macchina){this.macchina=macchina;}
    public Macchina getMacchina(){return this.macchina;}

}