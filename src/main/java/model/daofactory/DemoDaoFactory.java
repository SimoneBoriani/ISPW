package model.daofactory;

import model.acquistoauto.dao.DaoAcquistoAuto;
import model.macchina.dao.DaoMacchina;
import model.macchina.dao.DemoDaoMacchina;
import model.utente.dao.DaoUtente;
import model.utente.dao.DemoDaoUtente;

public class DemoDaoFactory extends DaoFactory {

    @Override
    public DaoMacchina createMacchinaDao() {
        return new DemoDaoMacchina();
    }

    @Override
    public DaoUtente createUtenteDao() {
        return new DemoDaoUtente();
    }

    @Override
    public DaoAcquistoAuto createAcquistoAutoDao() {
        return null;
    }
}
