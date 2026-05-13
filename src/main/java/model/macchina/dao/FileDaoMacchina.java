package model.macchina.dao;

import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import model.macchina.Macchina;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileDaoMacchina extends DaoMacchina {

    private static final String CSV_PATH = "src/main/resources/csv/car.csv";
    private static final String SEPARATOR = ",";

    private List<Macchina> loadAll() {
        List<Macchina> macchine = new ArrayList<>();
        try {
            File file = new File(CSV_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                return macchine;
            }

            List<String> lines = Files.readAllLines(Paths.get(CSV_PATH));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(SEPARATOR);
                Macchina m = new Macchina(
                        Integer.parseInt(d[0]), d[1], d[2], Integer.parseInt(d[3]),
                        d[4], d[5], Double.parseDouble(d[6]), d[7],
                        Integer.parseInt(d[8]), d[9]
                );
                m.setDisponibile(Boolean.parseBoolean(d[10]));
                macchine.add(m);
            }
        } catch (IOException e) {
            throw new GenericSystemException("Errore lettura CSV macchine", e);
        }
        return macchine;
    }

    private void saveAll(List<Macchina> macchine) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
            for (Macchina m : macchine) {
                pw.println(String.join(SEPARATOR,
                        String.valueOf(m.getId()), m.getModello(), m.getMarca(),
                        String.valueOf(m.getPosti()), m.getAlimentazione(), m.getTrasmissione(),
                        String.valueOf(m.getPrezzo()), m.getTipologia(), String.valueOf(m.getAnno()),
                        m.getImageUrl(), String.valueOf(m.getDisponibile())
                ));
            }
        } catch (IOException e) {
            throw new GenericSystemException("Errore scrittura CSV macchine", e);
        }
    }

    @Override
    public void remove(int idAuto) {
        List<Macchina> macchine = loadAll();
        macchine.removeIf(m -> m.getId() == idAuto);
        saveAll(macchine);
    }

    @Override
    public void insert(List<Macchina> autoDaSalvare) {
        List<Macchina> macchine = loadAll();
        int lastId = macchine.stream().mapToInt(Macchina::getId).max().orElse(0);

        for (Macchina m : autoDaSalvare) {
            m.setId(++lastId);
            m.setDisponibile(true);
            macchine.add(m);
        }
        saveAll(macchine);
    }

    @Override
    public List<Macchina> getCars() {
        return loadAll().stream().filter(Macchina::getDisponibile).toList();
    }

    @Override
    public List<Macchina> research(Macchina filtri) throws CarNotFoundException {
        List<Macchina> all = loadAll();

        List<Macchina> filtered = all.stream()
                .filter(Macchina::getDisponibile)
                .filter(m -> filtri.getModello() == null || filtri.getModello().isBlank() ||
                        m.getModello().toLowerCase().contains(filtri.getModello().toLowerCase()))
                .filter(m -> filtri.getMarca() == null || filtri.getMarca().isBlank() ||
                        m.getMarca().toLowerCase().contains(filtri.getMarca().toLowerCase()))
                .filter(m -> filtri.getAlimentazione() == null || filtri.getAlimentazione().isBlank() ||
                        m.getAlimentazione().equalsIgnoreCase(filtri.getAlimentazione()))
                .filter(m -> filtri.getPrezzo() <= 0 || m.getPrezzo() <= filtri.getPrezzo())
                .toList();

        if (filtered.isEmpty()) throw new CarNotFoundException("Nessuna auto trovata.");
        return filtered;
    }

    @Override
    public void update(Macchina macchina) {
        List<Macchina> all = loadAll();
        boolean updated = false;

        for (Macchina m : all) {
            if (m.getId() == macchina.getId()) {
                if (macchina.getModello() != null && !macchina.getModello().isBlank()) m.setModello(macchina.getModello());
                if (macchina.getMarca() != null && !macchina.getMarca().isBlank()) m.setMarca(macchina.getMarca());
                if (macchina.getAlimentazione() != null && !macchina.getAlimentazione().isBlank()) m.setAlimentazione(macchina.getAlimentazione());
                if (macchina.getTrasmissione() != null && !macchina.getTrasmissione().isBlank()) m.setTrasmissione(macchina.getTrasmissione());
                if (macchina.getPrezzo() > 0) m.setPrezzo(macchina.getPrezzo());
                m.setDisponibile(macchina.getDisponibile());

                updated = true;
                break;
            }
        }
        if (updated) saveAll(all);
    }
}