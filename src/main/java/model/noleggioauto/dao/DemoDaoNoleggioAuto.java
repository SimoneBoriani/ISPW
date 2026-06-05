package model.noleggioauto.dao;

import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;
import utils.ConfigLoader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DemoDaoNoleggioAuto extends DaoNoleggioAuto {

    private static final List<NoleggioAuto> noleggi = new ArrayList<>();
    private static final String TERMINATO = "TERMINATO";
    private static final String ATTIVO = "ATTIVO";

    @Override
    public void rentRequest(Utente utente, Macchina macchina, int giorni) {

        Macchina m = noleggi.stream()
                .map(NoleggioAuto::getMacchina)
                .filter(c -> c.getId() == macchina.getId())
                .findFirst()
                .orElse(macchina);

        NoleggioAuto n = new NoleggioAuto();
        n.setIdNoleggio(noleggi.size() + 1);
        n.setUtente(utente);
        n.setMacchina(m);
        n.setDataInizio(LocalDate.now(ConfigLoader.getTimeZone()));
        n.setDataFine(LocalDate.now(ConfigLoader.getTimeZone()).plusDays(giorni));
        n.setPrezzoTotalePagato(macchina.getPrezzo());
        n.setStato(ATTIVO);
        n.setMotivoChiusura("");

        noleggi.add(n);
        utente.setSaldo(utente.getSaldo() - macchina.getPrezzo());
    }

    @Override
    public void rentRequestEsternoConfermato(Utente utente, Macchina macchina, int giorni) {

        Macchina m = noleggi.stream()
                .map(NoleggioAuto::getMacchina)
                .filter(c -> c.getId() == macchina.getId())
                .findFirst()
                .orElse(macchina);

        NoleggioAuto n = new NoleggioAuto();
        n.setIdNoleggio(noleggi.size() + 1);
        n.setUtente(utente);
        n.setMacchina(m);
        n.setDataInizio(LocalDate.now(ConfigLoader.getTimeZone()));
        n.setDataFine(LocalDate.now(ConfigLoader.getTimeZone()).plusDays(giorni));
        n.setPrezzoTotalePagato(macchina.getPrezzo());
        n.setStato(ATTIVO);
        n.setMotivoChiusura("");

        noleggi.add(n);
    }

    @Override
    public void terminaNoleggio(int idNoleggio, String motivo) {
        for (NoleggioAuto n : noleggi) {
            if (n.getIdNoleggio() == idNoleggio && ATTIVO.equals(n.getStato())) {
                n.setStato(TERMINATO);
                n.setMotivoChiusura(motivo);

                if ("Chiusura Anticipata".equals(motivo)) {
                    n.setDataFine(LocalDate.now(ConfigLoader.getTimeZone()));
                }

                n.getMacchina().setDisponibile(true);
                break;
            }
        }
    }

    @Override
    public List<NoleggioAuto> getUserCars(Utente utente) {
        return noleggi.stream()
                .filter(n -> n.getUtente().getIdUser() == utente.getIdUser() && ATTIVO.equals(n.getStato()))
                .toList();
    }

    @Override
    public void sbloccaAutoScadute() {
        LocalDate oggi = LocalDate.now(ConfigLoader.getTimeZone());
        for (NoleggioAuto n : noleggi) {
            if (ATTIVO.equals(n.getStato()) && n.getDataFine() != null && n.getDataFine().isBefore(oggi)) {
                n.setStato(TERMINATO);
                n.setMotivoChiusura("Chiusura Naturale");
                n.getMacchina().setDisponibile(true);
            }
        }
    }

    @Override
    public List<NoleggioAuto> getRented() {
        return new ArrayList<>(noleggi);
    }

    @Override
    public Map<LocalDate, Double> getProfittiPerData() {
        Map<LocalDate, Double> profitti = new TreeMap<>();
        for (NoleggioAuto n : noleggi) {
            if (TERMINATO.equals(n.getStato()) && n.getDataFine() != null) {
                profitti.merge(n.getDataFine(), n.getPrezzoTotalePagato(), Double::sum);
            }
        }
        return profitti;
    }
}