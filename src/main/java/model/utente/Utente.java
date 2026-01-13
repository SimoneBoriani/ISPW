package model.utente;

public class Utente {

    private int idUser; //not null
    private String username;//not null
    private String userPassword;//not null
    private String nome;
    private String cognome;
    private String email;
    boolean titolare;//not null

    public Utente(int idUser, String username, String userPassword, String nome, String cognome, String email) {

        this.idUser = idUser;
        this.username = username;
        this.userPassword = userPassword;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.titolare = false;
    }
    public Utente(int user_id, String username, String userPassword,boolean titolare) {
        this.idUser = user_id;
        this.username = username;
        this.userPassword = userPassword;
        this.titolare = titolare;
    }

    public int getIdUser() {return idUser;}
    public String getUsername() {return username;}
    public String getUserPassword() {return userPassword;}
    public String getNome() {return nome;}
    public String getCognome() {return cognome;}
    public String getEmail() {return email;}
    public boolean isTitolare() {return titolare;}
    public void setTitolare(boolean titolare) {this.titolare = titolare;}
}
