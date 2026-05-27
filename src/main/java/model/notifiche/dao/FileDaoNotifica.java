package model.notifiche.dao;

import model.notifiche.Notifica;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileDaoNotifica extends DaoNotifica {

    private static final Logger logger = Logger.getLogger(FileDaoNotifica.class.getName());

    private static final String FILE_PATH = "notifiche_data.csv";
    private static final String DELIMITER = "|#|";

    private static List<Notifica> databaseNotifiche = new ArrayList<>();
    private static int idCounter = 1;

    static {
        caricaDaFile();
    }

    private static void caricaDaFile() {
        Path path = Paths.get(FILE_PATH);

        if (!Files.exists(path)) {
            try {
                if (path.getParent() != null) Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Impossibile creare il file: " + FILE_PATH, e);
            }
            return;
        }

        try {
            for (String riga : Files.readAllLines(path)) {
                String[] dati = riga.split("\\|#\\|");

                if (dati.length < 7) continue;

                Notifica n = new Notifica(
                        Integer.parseInt(dati[0]),
                        dati[1],
                        dati[2],
                        dati[3].replace("\\n", "\n"),
                        Notifica.Tipo.valueOf(dati[4]),
                        Boolean.parseBoolean(dati[5]),
                        LocalDateTime.parse(dati[6])
                );

                if (dati.length >= 8 && !dati[7].equals("null")) {
                    n.setAuto(dati[7]);
                }

                databaseNotifiche.add(n);
            }

            idCounter = databaseNotifiche.stream()
                    .mapToInt(Notifica::getId)
                    .max()
                    .orElse(0) + 1;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura del file testuale", e);
        }
    }

    private static void salvaSuFile() {
        Path path = Paths.get(FILE_PATH);
        List<String> righe = new ArrayList<>();

        for (Notifica n : databaseNotifiche) {
            String testoSicuro = n.getTesto() != null ? n.getTesto().replace("\n", "\\n") : "";
            String autoVal = n.getAuto() != null ? n.getAuto() : "null";

            String riga = n.getId() + DELIMITER +
                    n.getMittente() + DELIMITER +
                    n.getDestinatario() + DELIMITER +
                    testoSicuro + DELIMITER +
                    n.getTipo().name() + DELIMITER +
                    n.isLetta() + DELIMITER +
                    n.getDataCreazione().toString() + DELIMITER +
                    autoVal;

            righe.add(riga);
        }

        try {
            Files.write(path, righe);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio su file testuale", e);
        }
    }

    private static synchronized int getNextId() {
        return idCounter++;
    }

    @Override
    public void inserisci(Notifica notifica) {
        LocalDateTime data = notifica.getDataCreazione() != null ? notifica.getDataCreazione() : LocalDateTime.now();

        Notifica nuovaNotifica = new Notifica(
                getNextId(),
                notifica.getMittente(),
                notifica.getDestinatario(),
                notifica.getTesto(),
                notifica.getTipo(),
                notifica.isLetta(),
                data
        );
        nuovaNotifica.setAuto(notifica.getAuto());

        databaseNotifiche.add(nuovaNotifica);
        salvaSuFile();
    }

    @Override
    public List<Notifica> getComunicazioniPerDestinatario(String id) {
        return databaseNotifiche.stream()
                .filter(n -> n.getDestinatario().equals(id))
                .sorted(Comparator.comparing(Notifica::getDataCreazione).reversed())
                .toList();
    }

    @Override
    public List<Notifica> getNonLette(String destinatario) {
        return databaseNotifiche.stream()
                .filter(n -> n.getDestinatario().equals(destinatario) && !n.isLetta())
                .sorted(Comparator.comparing(Notifica::getDataCreazione).reversed())
                .toList();
    }

    @Override
    public void segnaComeLetta(int id) {
        Optional<Notifica> notificaTrovata = databaseNotifiche.stream()
                .filter(n -> n.getId() == id)
                .findFirst();

        if (notificaTrovata.isPresent()) {
            notificaTrovata.get().setLetta(true);
            salvaSuFile();
        }
    }

    public void eliminaNotifica(int idNotifica) {
        boolean rimosso = databaseNotifiche.removeIf(n -> n.getId() == idNotifica);
        if (rimosso) {
            salvaSuFile();
        }
    }
}