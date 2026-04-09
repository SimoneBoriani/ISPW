package model.macchina.dao;

import bean.CatalogoBean;
import model.macchina.Macchina;

import java.util.List;

public abstract class DaoMacchina {

    public abstract void insert(Macchina macchina);
    public abstract List<Macchina> research(String brand,String model, String alimentation, int kmMax);
    public abstract List<Macchina> getCars();

}
