package model.daofactory;

import model.macchina.dao.DaoMacchina;
import model.macchina.dao.DbmsDaoMacchina;
import model.utente.dao.DaoUtente;
import model.utente.dao.DbmsDaoUtente;

public class DbmsDaoFactory extends DaoFactory {

    @Override
    public DaoMacchina createMacchinaDao() {
        return new DbmsDaoMacchina();
    }

    @Override
    public DaoUtente createUtenteDao() {return new DbmsDaoUtente();}
}
