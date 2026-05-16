package model.noleggioauto.dao;

import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DemoDaoNoleggioAuto extends DaoNoleggioAuto {

    private static final List<NoleggioAuto> noleggi = new ArrayList<>();
    private static final String STATE = "ATTIVO";
    private static final String STATE1 = "TERMINATO";
    private static final String CHIUSURA_ANTICIPATA = "Chiusura Anticipata";

    @Override
    public void rentRequest(Utente utente, Macchina macchina, int giorni) {
        NoleggioAuto nuovoNoleggio = new NoleggioAuto();
        nuovoNoleggio.setUtente(utente);
        nuovoNoleggio.setMacchina(macchina);
        nuovoNoleggio.setDataFine(LocalDate.now().plusDays(giorni));
        nuovoNoleggio.setPrezzoTotalePagato(macchina.getPrezzo() * giorni);
        nuovoNoleggio.setStato(STATE);

        double saldoFinale = utente.getSaldo() - macchina.getPrezzo();

        utente.setSaldo(saldoFinale);
        macchina.setDisponibile(false);

        noleggi.add(nuovoNoleggio);
    }

    @Override
    public boolean checkInfo(Utente utente, Macchina macchina) {
        return utente.getSaldo() >= macchina.getPrezzo();
    }

    @Override
    public List<Macchina> getUserCars(Utente utente) {
        return noleggi.stream()
                .filter(n -> n.getUtente().getIdUser() == utente.getIdUser())
                .filter(n -> STATE.equals(n.getStato()))
                .map(NoleggioAuto::getMacchina)
                .toList();
    }

    @Override
    public void sbloccaAutoScadute(String motivo) {
        LocalDate oggi = LocalDate.now();

        for (NoleggioAuto n : noleggi) {
            if (STATE.equals(n.getStato()) && (n.getDataFine().isBefore(oggi) || CHIUSURA_ANTICIPATA.equals(motivo))) {
                n.setStato(STATE1);
                n.setMotivoChiusura(motivo);
                n.getMacchina().setDisponibile(true);

                if (CHIUSURA_ANTICIPATA.equals(motivo)) {
                    n.setDataFine(oggi);
                }
            }
        }
    }

    @Override
    public List<NoleggioAuto> getRented() {
        return noleggi;
    }

    @Override
    public Map<LocalDate, Double> getProfittiPerData() {
        Map<LocalDate, Double> profitti = new TreeMap<>();

        for (NoleggioAuto n : noleggi) {
            if (STATE1.equals(n.getStato())) {
                LocalDate data = n.getDataFine();
                double importo = n.getPrezzoTotalePagato();

                profitti.put(data, profitti.getOrDefault(data, 0.0) + importo);
            }
        }
        return profitti;
    }
}