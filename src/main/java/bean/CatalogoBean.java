package bean;

import model.macchina.Macchina;
import model.macchina.dao.DaoMacchine;
import java.util.List;

public class CatalogoBean {
    public static List<Macchina> getCars() {
        return DaoMacchine.getCars();
    }
}
