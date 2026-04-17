package model.utente;

public class Utente {

    private int idUser;
    private String username;
    private String userPassword;
    private String nome;
    private String cognome;
    private int autoPossedute;
    private double saldo;
    private String ruolo;

    public Utente(int idUser, String username, String userPassword, String nome,String cognome) {

        this.idUser = idUser;
        this.username = username;
        this.userPassword = userPassword;
        this.nome = nome;
        this.cognome = cognome;
    }
    public Utente(int idUser, String username, String userPassword, String nome,String cognome,int autoPossedute,double saldo,String ruolo) {

        this.idUser = idUser;
        this.username = username;
        this.userPassword = userPassword;
        this.nome = nome;
        this.cognome = cognome;
        this.ruolo = ruolo;
        this.autoPossedute = autoPossedute;
        this.saldo = saldo;

    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getUserPassword() {return userPassword;}
    public void setUserPassword(String userPassword) {this.userPassword = userPassword;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getCognome() {return cognome;}
    public void setCognome(String cognome) {this.cognome = cognome;}

    public int getAutoPossedute() {return autoPossedute;}
    public void setAutoPossedute(int autoPossedute) {this.autoPossedute = autoPossedute;}

    public double getSaldo(){return saldo;}
    public void setSaldo(double saldo){this.saldo = saldo;}

    public void setRuolo(String ruolo){this.ruolo = ruolo;}
    public String getRuolo() {return ruolo;}
}
