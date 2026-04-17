package model.macchina.dao;

import model.macchina.Macchina;

import java.util.ArrayList;
import java.util.List;

public class DemoDaoMacchina extends DaoMacchina {

    private static final List<Macchina> demoCars=new ArrayList<>();

    @Override
    public void insert(Macchina macchina) {
        demoCars.add(macchina);
    }

    @Override
    public List<Macchina> research(String brand, String model, String alimentation, int kmMax) {
        return demoCars.stream()
                .filter(m -> (brand == null || brand.isEmpty() || m.getCasa().equalsIgnoreCase(brand)))
            .filter(m -> (model == null || model.isEmpty() || m.getModello().equalsIgnoreCase(model)))
            .filter(m -> (alimentation == null || alimentation.isEmpty() || m.getAlimentazione().equalsIgnoreCase(alimentation)))
            .filter(m -> (kmMax <= 0 || m.getKm() <= kmMax))
            .toList();
    }

    @Override
    public List<Macchina> getCars() {
        return demoCars;
    }
}
