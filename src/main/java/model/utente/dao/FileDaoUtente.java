package model.utente.dao;

import exceptions.GenericSystemException;
import model.utente.Utente;
import utils.SessionSingleton;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileDaoUtente extends DaoUtente {

    private static final String CSV_PATH = "src/main/resources/csv/user.csv";
    private static final String SEPARATOR = ",";

    private List<Utente> loadAll() {
        List<Utente> utenti = new ArrayList<>();
        try {
            File file = new File(CSV_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                return utenti;
            }

            List<String> lines = Files.readAllLines(Paths.get(CSV_PATH));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(SEPARATOR);
                utenti.add(new Utente(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        Integer.parseInt(data[5]),
                        Double.parseDouble(data[6]),
                        data[7]
                ));
            }
        } catch (IOException e) {
            throw new GenericSystemException("Errore nel caricamento del file utenti", e);
        }
        return utenti;
    }

    private void saveAll(List<Utente> utenti) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
            for (Utente u : utenti) {
                pw.println(String.join(SEPARATOR,
                        String.valueOf(u.getIdUser()),
                        u.getUsername(),
                        u.getUserPassword(),
                        u.getNome() != null ? u.getNome() : "",
                        u.getCognome() != null ? u.getCognome() : "",
                        String.valueOf(u.getAutoPossedute()),
                        String.valueOf(u.getSaldo()),
                        u.getRuolo()
                ));
            }
        } catch (IOException e) {
            throw new GenericSystemException("Errore nel salvataggio del file utenti", e);
        }
    }

    @Override
    public void insertUtente(Utente utente) {

        List<Utente> utenti = loadAll();
        int nextId = utenti.stream().mapToInt(Utente::getIdUser).max().orElse(0) + 1;
        utente.setIdUser(nextId);
        utente.setRuolo(nextId == 1 ? "ADMIN" : "USER");
        utenti.add(utente);
        saveAll(utenti);
    }

    @Override
    public Utente researchUser(Utente utente) {
        return loadAll().stream()
                .filter(u -> u.getUsername().equals(utente.getUsername()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean authenticateUser(Utente utente) {
        return loadAll().stream()
                .anyMatch(u -> u.getUsername().equals(utente.getUsername())
                        && u.getUserPassword().equals(utente.getUserPassword()));
    }

    @Override
    public void update(Utente utente) {
        List<Utente> utenti = loadAll();

        Utente target = utenti.stream()
                .filter(u -> u.getIdUser() == utente.getIdUser())
                .findFirst()
                .orElse(null);

        if (target != null) {
            applyUpdates(utente, target);
            saveAll(utenti);
            sincronizzaSessione(utente);
        }
    }

    private void applyUpdates(Utente source, Utente target) {

        updateString(source.getUsername(), target::setUsername);
        updateString(source.getUserPassword(), target::setUserPassword);
        updateString(source.getNome(), target::setNome);
        updateString(source.getCognome(), target::setCognome);
        if (source.getAutoPossedute() > 0) {
            target.setAutoPossedute(source.getAutoPossedute());
        }
        if (source.getSaldo() != 0.0) {
            target.setSaldo(target.getSaldo() + source.getSaldo());
        }
    }

    private void updateString(String value, java.util.function.Consumer<String> setter) {
        if (value != null && !value.trim().isEmpty()) {
            setter.accept(value.trim());
        }
    }

    private void sincronizzaSessione(Utente utente) {
        Utente sessionUser = SessionSingleton.getInstance().getUtenteCorrente();
        if (sessionUser == null) return;

        if (utente.getUsername() != null && !utente.getUsername().trim().isEmpty()) sessionUser.setUsername(utente.getUsername());
        if (utente.getNome() != null && !utente.getNome().trim().isEmpty()) sessionUser.setNome(utente.getNome());
        if (utente.getCognome() != null && !utente.getCognome().trim().isEmpty()) sessionUser.setCognome(utente.getCognome());

        if (utente.getSaldo() != 0.0) {
            sessionUser.setSaldo(sessionUser.getSaldo() + utente.getSaldo());
        }
    }
}
