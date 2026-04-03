package model.daofactory;

import model.macchina.dao.DaoMacchina;
import model.macchina.dao.DbmsDaoMacchina;

public class DbmsDaoFactory extends DaoFactory {

    @Override
    public DaoMacchina createMacchinaDao() {
        return new DbmsDaoMacchina();
    }
    //Implementa gli altri metodi
}
