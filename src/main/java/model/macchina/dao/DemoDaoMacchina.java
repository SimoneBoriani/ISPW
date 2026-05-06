package model.macchina.dao;

import exceptions.CarNotFoundException;
import model.macchina.Macchina;

import java.util.ArrayList;
import java.util.List;

public class DemoDaoMacchina extends DaoMacchina {

    private static final List<Macchina> demoCars=new ArrayList<>();

    @Override
    public void insert(List<Macchina> macchina) {
        demoCars.addAll(macchina);
    }

    @Override
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {


        String model = filtriAuto.getModello();
        String alimentation = filtriAuto.getAlimentazione();

        List<Macchina> results = demoCars.stream()
                .filter(m -> (model == null || model.trim().isEmpty() || m.getModello().toLowerCase().contains(model.toLowerCase())))
                .filter(m -> (alimentation == null || alimentation.trim().isEmpty() || m.getAlimentazione().equalsIgnoreCase(alimentation)))
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
    public void update(Macchina macchina) {

    }
}
