package bean;


import model.utente.Utente;
import model.utente.dao.DaoUtente;

import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginBean {

    Logger logger = Logger.getLogger(getClass().getName());
    String username;
    String password;
    int autoPossedute;
    double saldo;
    String nome;
    String cognome;


    public LoginBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Utente sendInfoInsert(){
        try{
            return DaoUtente.insertUtente(this);
        }catch(SQLException e){
            logger.severe(e.getMessage());
            return null;
        }
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    public String getCognome() {return cognome;}
    public void setCognome(String cognome) {this.cognome = cognome;}

    public int getAutoPossedute() {return autoPossedute;}
    public void setAutoPossedute(int autoPossedute) {this.autoPossedute = autoPossedute;}

    public double getSaldo(){return saldo;}
    public void setSaldo(double saldo){this.saldo = saldo;}
}
