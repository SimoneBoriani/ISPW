package model.utente;

public class Utente {

    private int user_id; //not null
    private String username;//not null
    private String user_password;//not null
    private String nome;
    private String cognome;
    private String email;
    boolean titolare;//not null

    public Utente(int user_id, String username, String user_password, String nome, String cognome, String email) {

        this.user_id = user_id;
        this.username = username;
        this.user_password = user_password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.titolare = false;
    }
    public Utente(int user_id, String username, String user_password,boolean titolare) {
        this.user_id = user_id;
        this.username = username;
        this.user_password = user_password;
        this.titolare = titolare;
    }

    public int getUser_id() {return user_id;}
    public String getUsername() {return username;}
    public String getUser_password() {return user_password;}
    public String getNome() {return nome;}
    public String getCognome() {return cognome;}
    public String getEmail() {return email;}
    public boolean isTitolare() {return titolare;}
    public void setTitolare(boolean titolare) {this.titolare = titolare;}
}
