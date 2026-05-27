package model.notifiche.dao;

import model.notifiche.Notifica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DemoDaoNotifica extends DaoNotifica {

    private static final List<Notifica> databaseNotifiche = new ArrayList<>();
    private static int idCounter = 1;

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

        notificaTrovata.ifPresent(n -> n.setLetta(true));
    }

    public void eliminaNotifica(int idNotifica) {
        databaseNotifiche.removeIf(n -> n.getId() == idNotifica);
    }
}