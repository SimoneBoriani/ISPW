package model.noleggioauto.dao;

import exceptions.GenericSystemException;
import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class FileDaoNoleggioAuto extends DaoNoleggioAuto {

    private static final String CSV_PATH = "src/main/resources/csv/noleggi.csv";
    private static final String CSV_CAR = "src/main/resources/csv/car.csv";
    private static final String CSV_USER = "src/main/resources/csv/user.csv";
    private static final String SEPARATOR = ",";
    private static final String TERMINATO = "TERMINATO";
    private static final String ATTIVO = "ATTIVO";

    @Override
    public void rentRequest(Utente utente, Macchina macchina, int giorni) {
        try {
            Utente u = findUserById(utente.getIdUser());
            Macchina m = findCarById(macchina.getId());

            if (m == null || !m.getDisponibile()) throw new GenericSystemException("Auto non disponibile");
            if (u == null || u.getSaldo() < macchina.getPrezzo()) throw new GenericSystemException("Saldo insufficiente");

            List<NoleggioAuto> tutti = new ArrayList<>(loadAllRentals());

            int nextId = tutti.stream().mapToInt(NoleggioAuto::getIdNoleggio).max().orElse(0) + 1;

            NoleggioAuto n = new NoleggioAuto();
            n.setIdNoleggio(nextId);
            n.setUtente(u);
            n.setMacchina(m);
            n.setDataInizio(LocalDate.now());
            n.setDataFine(LocalDate.now().plusDays(giorni));

            n.setPrezzoTotalePagato(macchina.getPrezzo());
            n.setStato(ATTIVO);
            n.setMotivoChiusura("");

            tutti.add(n);
            saveAllRentals(tutti);
            updateFileField(CSV_CAR, m.getId(), 10, "false");
            updateFileField(CSV_USER, u.getIdUser(), 6, String.valueOf(u.getSaldo() - macchina.getPrezzo()));

        } catch (Exception e) {
            throw new GenericSystemException("Errore durante la transazione di noleggio su file", e);
        }
    }

    @Override
    public void terminaNoleggio(int idNoleggio, String motivo) {
        List<NoleggioAuto> rentals = loadAllRentals();
        boolean changed = false;

        for (NoleggioAuto n : rentals) {
            if (n.getIdNoleggio() == idNoleggio && ATTIVO.equals(n.getStato())) {
                n.setStato(TERMINATO);
                n.setMotivoChiusura(motivo);

                if ("Chiusura Anticipata".equals(motivo)) {
                    n.setDataFine(LocalDate.now());
                }

                try {
                    updateFileField(CSV_CAR, n.getMacchina().getId(), 10, "true");
                } catch (Exception e) {
                    throw new GenericSystemException("Errore di connessione durante lo sblocco auto", e);
                }
                changed = true;
                break;
            }
        }
        if (changed) saveAllRentals(rentals);
    }

    @Override
    public List<NoleggioAuto> getUserCars(Utente utente) {
        return loadAllRentals().stream()
                .filter(n -> n.getUtente().getIdUser() == utente.getIdUser() && ATTIVO.equals(n.getStato()))
                .toList();
    }

    @Override
    public void sbloccaAutoScadute() {
        List<NoleggioAuto> rentals = loadAllRentals();
        boolean changed = false;
        LocalDate oggi = LocalDate.now();

        for (NoleggioAuto n : rentals) {
            if (ATTIVO.equals(n.getStato()) && n.getDataFine() != null && n.getDataFine().isBefore(oggi)) {
                n.setStato(TERMINATO);
                n.setMotivoChiusura("Chiusura Naturale");
                try {
                    updateFileField(CSV_CAR, n.getMacchina().getId(), 10, "true");
                } catch (Exception e) {
                    //gestione ex
                }
                changed = true;
            }
        }
        if (changed) saveAllRentals(rentals);
    }

    @Override
    public List<NoleggioAuto> getRented() {
        return loadAllRentals();
    }

    @Override
    public Map<LocalDate, Double> getProfittiPerData() {
        Map<LocalDate, Double> profitti = new TreeMap<>();
        for (NoleggioAuto n : loadAllRentals()) {
            if (TERMINATO.equals(n.getStato()) && n.getDataFine() != null) {
                profitti.merge(n.getDataFine(), n.getPrezzoTotalePagato(), Double::sum);
            }
        }
        return profitti;
    }

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
            throw new GenericSystemException("Errore nel recupero dello storico noleggi dal file", e);
        }
    }

    private void saveAllRentals(List<NoleggioAuto> rentals) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
            for (NoleggioAuto n : rentals) {
                pw.println(String.join(SEPARATOR,
                        String.valueOf(n.getIdNoleggio()),
                        String.valueOf(n.getUtente().getIdUser()),
                        String.valueOf(n.getMacchina().getId()),
                        n.getDataInizio() != null ? n.getDataInizio().toString() : "",
                        n.getDataFine() != null ? n.getDataFine().toString() : "",
                        String.valueOf(n.getPrezzoTotalePagato()),
                        n.getStato(),
                        n.getMotivoChiusura() != null ? n.getMotivoChiusura() : ""
                ));
            }
        } catch (IOException e) {
            throw new GenericSystemException("Errore salvataggio file noleggi", e);
        }
    }

    private NoleggioAuto mapToNoleggio(String line, Map<Integer, Utente> uMap, Map<Integer, Macchina> mMap) {
        String[] d = line.split(SEPARATOR);
        if (d.length < 7) return null;

        Utente u = uMap.get(Integer.parseInt(d[1]));
        Macchina m = mMap.get(Integer.parseInt(d[2]));

        if (u == null || m == null) return null;

        NoleggioAuto n = new NoleggioAuto();
        n.setIdNoleggio(Integer.parseInt(d[0]));
        n.setUtente(u);
        n.setMacchina(m);
        if (!d[3].isEmpty()) n.setDataInizio(LocalDate.parse(d[3]));
        if (!d[4].isEmpty()) n.setDataFine(LocalDate.parse(d[4]));
        n.setPrezzoTotalePagato(Double.parseDouble(d[5]));
        n.setStato(d[6]);
        n.setMotivoChiusura(d.length > 7 ? d[7] : "");

        return n;
    }

    private Map<Integer, Utente> loadUtentiMap() throws IOException {

        Map<Integer, Utente> map = new HashMap<>();
        Path path = Paths.get(CSV_USER);

        if (!Files.exists(path)) return map;

        for (String l : Files.readAllLines(path)) {
            String[] d = l.split(SEPARATOR);

            if (l.isBlank() || d.length < 8) {
                continue;
            }

            try {
                Utente utente = new Utente();
                utente.setIdUser(Integer.parseInt(d[0]));
                utente.setUsername(d[1]);
                utente.setUserPassword(d[2]);
                utente.setNome(d[3]);
                utente.setCognome(d[4]);
                utente.setAutoPossedute(Integer.parseInt(d[5]));
                utente.setSaldo(Double.parseDouble(d[6]));
                utente.setRuolo(d[7]);

                map.put(utente.getIdUser(), utente);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new IOException("Errore nel formato della riga CSV: " + l, e);
            }
        }
        return map;
    }

    private Map<Integer, Macchina> loadMacchineMap() throws IOException {
        Map<Integer, Macchina> map = new HashMap<>();
        if (!Files.exists(Paths.get(CSV_CAR))) return map;

        for (String l : Files.readAllLines(Paths.get(CSV_CAR))) {
            if (l.trim().isEmpty()) continue;
            String[] d = l.split(SEPARATOR);

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
            map.put(m.getId(), m);
        }
        return map;
    }

    private Utente findUserById(int id) throws IOException {
        return loadUtentiMap().get(id);
    }

    private Macchina findCarById(int id) throws IOException {
        return loadMacchineMap().get(id);
    }

    private void updateFileField(String path, int idRicercato, int indexCampo, String nuovoValore) throws IOException {
        if (!Files.exists(Paths.get(path))) return;
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
}