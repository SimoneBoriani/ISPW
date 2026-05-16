package bean;

public class ProfileBean {

    int id;
    String username;
    String password;
    double saldo;
    String nome;
    String cognome;
    String ruolo;

    public ProfileBean() {
        //Costruttore
    }

    public ProfileBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setId(int id) {this.id = id;}
    public int getId() {return id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getCognome() {return cognome;}
    public void setCognome(String cognome) {this.cognome = cognome;}

    public double getSaldo(){return saldo;}
    public void setSaldo(double saldo){this.saldo = saldo;}

    public String getRuolo() {return ruolo;}
    public void setRuolo(String ruolo) {this.ruolo = ruolo;}
}
