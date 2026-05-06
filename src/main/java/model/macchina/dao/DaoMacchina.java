package model.macchina.dao;

import model.macchina.Macchina;

import java.util.List;

public abstract class DaoMacchina {

    public abstract void insert(List<Macchina> macchina);
    public abstract List<Macchina> research(Macchina macchina);
    public abstract List<Macchina> getCars();
    public abstract void remove(int id);
    public abstract void update(Macchina macchina);

}
