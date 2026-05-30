package model.macchina.dao;

import exceptions.CarNotFoundException;
import exceptions.GenericSystemException;
import model.macchina.Macchina;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileDaoMacchina extends DaoMacchina {

    private static final String CSV_PATH = "src/main/resources/csv/car.csv";
    private static final String SEPARATOR = ",";


    // 0: ID | 1: Marca | 2: Modello | 3: Anno | 4: Tipologia | 5: Alimentazione | 6: Prezzo | 7: Trasmissione | 8: Posti | 9: ImageUrl | 10: Disponibile

    private List<Macchina> loadAll() {
        List<Macchina> macchine = new ArrayList<>();
        try {
            File file = new File(CSV_PATH);
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                boolean creato = file.createNewFile();
                if (!creato) {
                    throw new GenericSystemException("Impossibile creare il file CSV delle macchine.");
                }

                return macchine;
            }

            List<String> lines = Files.readAllLines(Paths.get(CSV_PATH));
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    String[] d = line.split(SEPARATOR);

                    if (d.length >= 11) {
                        Macchina m = new Macchina();

                        m.setId(Integer.parseInt(d[0]));
                        m.setMarca(d[1]);
                        m.setModello(d[2]);
                        m.setAnno(Integer.parseInt(d[3]));
                        m.setTipologia(d[4]);
                        m.setAlimentazione(d[5]);
                        m.setPrezzo(Double.parseDouble(d[6]));
                        m.setTrasmissione(d[7]);
                        m.setPosti(Integer.parseInt(d[8]));
                        m.setImageUrl(d[9]);
                        m.setDisponibile(Boolean.parseBoolean(d[10]));

                        macchine.add(m);
                    }
                }
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
                        String.valueOf(m.getId()),                                      // 0
                        m.getMarca() != null ? m.getMarca() : "",                       // 1
                        m.getModello() != null ? m.getModello() : "",                   // 2
                        String.valueOf(m.getAnno()),                                    // 3
                        m.getTipologia() != null ? m.getTipologia() : "",               // 4
                        m.getAlimentazione() != null ? m.getAlimentazione() : "",       // 5
                        String.valueOf(m.getPrezzo()),                                  // 6
                        m.getTrasmissione() != null ? m.getTrasmissione() : "",         // 7
                        String.valueOf(m.getPosti()),                                   // 8
                        m.getImageUrl() != null ? m.getImageUrl() : "",                 // 9
                        String.valueOf(m.getDisponibile())                              // 10
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

        if (filtri != null && filtri.getId() > 0) {
            List<Macchina> foundById = all.stream()
                    .filter(m -> m.getId() == filtri.getId())
                    .toList();

            if (foundById.isEmpty()) {
                throw new CarNotFoundException("Nessuna auto trovata con ID " + filtri.getId());
            }
            return foundById;
        }

        List<Macchina> filtered = all.stream()
                .filter(Macchina::getDisponibile)
                .filter(m -> filtri == null || filtri.getModello() == null || filtri.getModello().isBlank() ||
                        m.getModello().toLowerCase().contains(filtri.getModello().toLowerCase()))
                .filter(m -> filtri == null || filtri.getMarca() == null || filtri.getMarca().isBlank() ||
                        m.getMarca().toLowerCase().contains(filtri.getMarca().toLowerCase()))
                .filter(m -> filtri == null || filtri.getAlimentazione() == null || filtri.getAlimentazione().isBlank() ||
                        m.getAlimentazione().equalsIgnoreCase(filtri.getAlimentazione()))
                .filter(m -> filtri == null || filtri.getTipologia() == null || filtri.getTipologia().isBlank() ||
                        m.getTipologia().equalsIgnoreCase(filtri.getTipologia()))
                .filter(m -> filtri == null || filtri.getTrasmissione() == null || filtri.getTrasmissione().isBlank() ||
                        m.getTrasmissione().equalsIgnoreCase(filtri.getTrasmissione()))
                .filter(m -> filtri == null || filtri.getPrezzo() <= 0 || m.getPrezzo() <= filtri.getPrezzo())
                .toList();

        if (filtered.isEmpty()) {
            throw new CarNotFoundException("Nessuna auto trovata con questi filtri.");
        }

        return filtered;
    }

    @Override
    public void update(Macchina macchina) {
        List<Macchina> all = loadAll();

        Optional<Macchina> optionalMacchina = all.stream()
                .filter(m -> m.getId() == macchina.getId())
                .findFirst();

        if (optionalMacchina.isPresent()) {
            Macchina m = optionalMacchina.get();
            aggiornaCampi(m, macchina);
            saveAll(all);
        }
    }

    private void aggiornaCampi(Macchina target, Macchina source) {
        if (source.getModello() != null && !source.getModello().isBlank()) target.setModello(source.getModello());
        if (source.getMarca() != null && !source.getMarca().isBlank()) target.setMarca(source.getMarca());
        if (source.getAlimentazione() != null && !source.getAlimentazione().isBlank()) target.setAlimentazione(source.getAlimentazione());
        if (source.getTrasmissione() != null && !source.getTrasmissione().isBlank()) target.setTrasmissione(source.getTrasmissione());
        if (source.getPrezzo() > 0) target.setPrezzo(source.getPrezzo());

        if (source.getAnno() > 0) target.setAnno(source.getAnno());
        if (source.getPosti() > 0) target.setPosti(source.getPosti());
        if (source.getTipologia() != null && !source.getTipologia().isBlank()) target.setTipologia(source.getTipologia());
        if (source.getImageUrl() != null && !source.getImageUrl().isBlank()) target.setImageUrl(source.getImageUrl());
    }
}