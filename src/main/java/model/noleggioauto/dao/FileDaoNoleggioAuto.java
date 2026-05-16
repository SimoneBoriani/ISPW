package model.noleggioauto.dao;

import exceptions.GenericSystemException;
import model.macchina.Macchina;
import model.macchina.dao.FileDaoMacchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;
import model.utente.dao.FileDaoUtente;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class FileDaoNoleggioAuto extends DaoNoleggioAuto {

    private static final String CSV_PATH = "src/main/resources/csv/noleggi.csv";
    private static final String CSV_CAR = "src/main/resources/csv/car.csv";
    private static final String CSV_USER = "src/main/resources/csv/user.csv";
    private static final String STATO_ATTIVO = "ATTIVO";
    private static final String STATO_TERMINATO = "TERMINATO";
    private static final String CHIUSURA_ANTICIPATA = "Chiusura Anticipata";
    private static final String SEPARATOR = ",";

    private final FileDaoUtente daoUtente = new FileDaoUtente();
    private final FileDaoMacchina daoMacchina = new FileDaoMacchina();

    private List<NoleggioAuto> loadAllRentals() {
        try {
            if (!Files.exists(Paths.get(CSV_PATH))) return new ArrayList<>();

            Map<Integer, Utente> utentiMap = loadUtentiMap();
            Map<Integer, Macchina> macchineMap = loadMacchineMap();

            return Files.readAllLines(Paths.get(CSV_PATH)).stream()
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> mapToNoleggio(line, utentiMap, macchineMap))
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            throw new GenericSystemException("Errore critico lettura file noleggi: " + e.getMessage(), e);
        }
    }

    private NoleggioAuto mapToNoleggio(String line, Map<Integer, Utente> uMap, Map<Integer, Macchina> mMap) {
        String[] d = line.split(SEPARATOR);
        Utente u = uMap.get(Integer.parseInt(d[0]));
        Macchina m = mMap.get(Integer.parseInt(d[1]));

        if (u == null || m == null) return null;

        NoleggioAuto n = new NoleggioAuto();
        n.setUtente(u);
        n.setMacchina(m);
        n.setDataFine(LocalDate.parse(d[2]));
        n.setPrezzoTotalePagato(Double.parseDouble(d[3]));
        n.setStato(d[4]);
        n.setMotivoChiusura(d.length > 5 ? d[5] : "");
        return n;
    }

    private Map<Integer, Utente> loadUtentiMap() throws IOException {
        Map<Integer, Utente> map = new HashMap<>();
        if (!Files.exists(Paths.get(CSV_USER))) return map;

        for (String l : Files.readAllLines(Paths.get(CSV_USER))) {
            if (l.trim().isEmpty()) continue;
            String[] d = l.split(SEPARATOR);
            map.put(Integer.parseInt(d[0]), new Utente(
                    Integer.parseInt(d[0]), d[1], d[2], d[3], d[4],
                    Integer.parseInt(d[5]), Double.parseDouble(d[6]), d[7]
            ));
        }
        return map;
    }

    private Map<Integer, Macchina> loadMacchineMap() throws IOException {
        Map<Integer, Macchina> map = new HashMap<>();
        if (!Files.exists(Paths.get(CSV_CAR))) return map;

        for (String l : Files.readAllLines(Paths.get(CSV_CAR))) {
            if (l.trim().isEmpty()) continue;
            String[] d = l.split(SEPARATOR);
            Macchina m = new Macchina(
                    Integer.parseInt(d[0]), d[1], d[2], Integer.parseInt(d[3]),
                    d[4], d[5], Double.parseDouble(d[6]), d[7],
                    Integer.parseInt(d[8]), d[9]
            );
            m.setDisponibile(Boolean.parseBoolean(d[10]));
            map.put(m.getId(), m);
        }
        return map;
    }

    private void saveAllRentals(List<NoleggioAuto> rentals) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
            for (NoleggioAuto n : rentals) {
                pw.println(String.join(SEPARATOR,
                        String.valueOf(n.getUtente().getIdUser()),
                        String.valueOf(n.getMacchina().getId()),
                        n.getDataFine().toString(),
                        String.valueOf(n.getPrezzoTotalePagato()),
                        n.getStato(),
                        n.getMotivoChiusura() != null ? n.getMotivoChiusura() : ""
                ));
            }
        } catch (IOException e) {
            throw new GenericSystemException("Errore scrittura CSV noleggi", e);
        }
    }

    @Override
    public void rentRequest(Utente utentePassato, Macchina macchinaPassata, int giorni) {
        try {
            Utente utenteReale = findUserById(utentePassato.getIdUser());
            Macchina macchinaReale = findCarById(macchinaPassata.getId());

            if (macchinaReale == null || !macchinaReale.getDisponibile())
                throw new GenericSystemException("Auto non disponibile.");
            if (utenteReale == null || utenteReale.getSaldo() < macchinaReale.getPrezzo())
                throw new GenericSystemException("Saldo insufficiente.");

            List<NoleggioAuto> tuttiNoleggi = loadAllRentals();
            tuttiNoleggi.add(creaNuovoNoleggio(utenteReale, macchinaReale, giorni));
            saveAllRentals(tuttiNoleggi);

            updateFileField(CSV_CAR, macchinaReale.getId(), 10, "false");
            updateFileField(CSV_USER, utenteReale.getIdUser(), 6, String.valueOf(utenteReale.getSaldo() - macchinaReale.getPrezzo()));

        } catch (IOException | NumberFormatException e) {
            throw new GenericSystemException("Errore durante il noleggio: " + e.getMessage(), e);
        }
    }

    private NoleggioAuto creaNuovoNoleggio(Utente u, Macchina m, int giorni) {
        NoleggioAuto n = new NoleggioAuto();
        n.setUtente(u);
        n.setMacchina(m);
        n.setDataFine(LocalDate.now().plusDays(giorni));
        n.setPrezzoTotalePagato(m.getPrezzo());
        n.setStato(STATO_ATTIVO);
        return n;
    }

    private void updateFileField(String path, int idRicercato, int indexCampo, String nuovoValore) throws IOException {
        List<String> linee = Files.readAllLines(Paths.get(path));
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String linea : linee) {
                if (linea.trim().isEmpty()) continue;
                String[] colonne = linea.split(SEPARATOR);
                if (Integer.parseInt(colonne[0]) == idRicercato) {
                    colonne[indexCampo] = nuovoValore;
                    pw.println(String.join(SEPARATOR, colonne));
                } else {
                    pw.println(linea);
                }
            }
        }
    }

    private Utente findUserById(int id) throws IOException {
        return Files.readAllLines(Paths.get(CSV_USER)).stream()
                .filter(line -> !line.trim().isEmpty())
                .map(l -> l.split(SEPARATOR))
                .filter(d -> Integer.parseInt(d[0]) == id)
                .map(d -> new Utente(Integer.parseInt(d[0]), d[1], d[2], d[3], d[4], Integer.parseInt(d[5]), Double.parseDouble(d[6]), d[7]))
                .findFirst().orElse(null);
    }

    private Macchina findCarById(int id) throws IOException {
        return Files.readAllLines(Paths.get(CSV_CAR)).stream()
                .filter(line -> !line.trim().isEmpty())
                .map(l -> l.split(SEPARATOR))
                .filter(d -> Integer.parseInt(d[0]) == id)
                .map(d -> {
                    Macchina m = new Macchina(Integer.parseInt(d[0]), d[1], d[2], Integer.parseInt(d[3]), d[4], d[5], Double.parseDouble(d[6]), d[7], Integer.parseInt(d[8]), d[9]);
                    m.setDisponibile(Boolean.parseBoolean(d[10]));
                    return m;
                }).findFirst().orElse(null);
    }

    @Override
    public void sbloccaAutoScadute(String motivo) {
        List<NoleggioAuto> rentals = loadAllRentals();
        boolean changed = false;
        LocalDate oggi = LocalDate.now();

        for (NoleggioAuto n : rentals) {
            if (n.getStato().equals(STATO_ATTIVO) && (n.getDataFine().isBefore(oggi) || CHIUSURA_ANTICIPATA.equals(motivo))) {
                n.setStato(STATO_TERMINATO);
                n.setMotivoChiusura(motivo);

                if (CHIUSURA_ANTICIPATA.equals(motivo)) {
                    n.setDataFine(oggi);
                }

                n.getMacchina().setDisponibile(true);
                daoMacchina.update(n.getMacchina());
                changed = true;
            }
        }
        if (changed) saveAllRentals(rentals);
    }

    @Override
    public List<Macchina> getUserCars(Utente utente) {
        return loadAllRentals().stream()
                .filter(n -> n.getUtente().getIdUser() == utente.getIdUser() && n.getStato().equals(STATO_ATTIVO))
                .map(NoleggioAuto::getMacchina)
                .toList();
    }

    @Override
    public boolean checkInfo(Utente utente, Macchina macchina) {
        Utente u = daoUtente.researchUser(utente);
        return u != null && u.getSaldo() >= macchina.getPrezzo();
    }

    @Override
    public List<NoleggioAuto> getRented() {
        return loadAllRentals();
    }

    @Override
    public Map<LocalDate, Double> getProfittiPerData() {
        Map<LocalDate, Double> profitti = new TreeMap<>();
        for (NoleggioAuto n : loadAllRentals()) {
            if (STATO_TERMINATO.equals(n.getStato())) {
                profitti.merge(n.getDataFine(), n.getPrezzoTotalePagato(), Double::sum);
            }
        }
        return profitti;
    }
}