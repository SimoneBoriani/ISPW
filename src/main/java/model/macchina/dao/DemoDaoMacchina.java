package model.macchina.dao;

import exceptions.CarNotFoundException;
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
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {

        String brand = filtriAuto.getCasa();
        String model = filtriAuto.getModello();
        String alimentation = filtriAuto.getAlimentazione();
        int kmMax = filtriAuto.getKm();

        List<Macchina> results = demoCars.stream()
                .filter(m -> (brand == null || brand.trim().isEmpty() || m.getCasa().equalsIgnoreCase(brand)))
                .filter(m -> (model == null || model.trim().isEmpty() || m.getModello().toLowerCase().contains(model.toLowerCase())))
                .filter(m -> (alimentation == null || alimentation.trim().isEmpty() || m.getAlimentazione().equalsIgnoreCase(alimentation)))
                .filter(m -> (kmMax <= 0 || m.getKm() <= kmMax))
                .toList();

        if (results.isEmpty()) {
            throw new CarNotFoundException("Nessuna auto trovata con i filtri inseriti.");
        }

        return results;
    }
    @Override
    public List<Macchina> getCars() {
        return demoCars;
    }

    @Override
    public void remove(int id) {

    }

    @Override
    public void discount(int id, int sconto, int prezzo) {

    }

    @Override
    public void update(Macchina macchina) {

    }
}
