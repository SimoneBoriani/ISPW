package model.macchina.dao;

import exceptions.CarNotFoundException;
import model.macchina.Macchina;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class DemoDaoMacchina extends DaoMacchina {

    private static final List<Macchina> demoCars = new ArrayList<>();

    @Override
    public void insert(List<Macchina> macchina) {

        demoCars.addAll(macchina);
    }

    @Override
    public List<Macchina> research(Macchina filtriAuto) throws CarNotFoundException {
        String model = filtriAuto.getModello();
        String alimentation = filtriAuto.getAlimentazione();

        List<Macchina> results = demoCars.stream()
                .filter(Macchina::getDisponibile)
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

        return demoCars.stream()
                .filter(Macchina::getDisponibile)
                .toList();
    }

    @Override
    public void remove(int id) {
        demoCars.removeIf(m -> m.getId() == id);
    }

    @Override
    public void update(Macchina macchina) {
        for (Macchina m : demoCars) {
            if (m.getId() == macchina.getId()) {

                updateIfPresent(macchina.getModello(), m::setModello);
                updateIfPresent(macchina.getMarca(), m::setMarca);
                updateIfPresent(macchina.getAlimentazione(), m::setAlimentazione);
                updateIfPresent(macchina.getTrasmissione(), m::setTrasmissione);
                updateIfPresent(macchina.getTipologia(), m::setTipologia);
                updateIfPresent(macchina.getImageUrl(), m::setImageUrl);
                if (macchina.getPrezzo() > 0.0) {m.setPrezzo(macchina.getPrezzo());}
                updateIfPositive(macchina.getPosti(), m::setPosti);
                updateIfPositive(macchina.getAnno(), m::setAnno);
                m.setDisponibile(true);

                break;
            }
        }
    }

    private void updateIfPresent(String value, Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) {
            setter.accept(value.trim());
        }
    }

    private void updateIfPositive(int value, IntConsumer setter) {
        if (value > 0) {
            setter.accept(value);
        }
    }
}