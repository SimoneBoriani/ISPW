package bean;

import model.utente.dao.DbmsDaoUtente;

import java.sql.SQLException;

public class AggiungiSaldoBean {

    public void sendInfo(LoginBean login){
        try {
            DbmsDaoUtente.update(login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
