package model.daofactory;

import model.macchina.dao.DaoMacchina;
import model.macchina.dao.FileDaoMacchina;
import model.noleggioauto.dao.DaoNoleggioAuto;
import model.noleggioauto.dao.FileDaoNoleggioAuto;
import model.utente.dao.DaoUtente;
import model.utente.dao.FileDaoUtente;

public class FileDaoFactory extends DaoFactory{

    @Override
    public DaoMacchina createMacchinaDao() {
        return new FileDaoMacchina();
    }

    @Override
    public DaoUtente createUtenteDao() {
        return new FileDaoUtente();
    }

    @Override
    public DaoNoleggioAuto createNoleggioAutoDao() {
        return new FileDaoNoleggioAuto();
    }
}
