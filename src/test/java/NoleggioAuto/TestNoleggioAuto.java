package NoleggioAuto;

import bean.NoleggioAutoBean;
import controller.NoleggioController;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;
import org.junit.jupiter.api.Test;
import view.factory.ControllerFactory;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestNoleggioAuto {

    private NoleggioController noleggioController = ControllerFactory.getGraphicalSingletonFactory().createNoleggioController();

    @Test
    void test_Utente_Con_Saldo_Sufficiente(){

        NoleggioAutoBean noleggio=new NoleggioAutoBean();

        Utente utente = new Utente();

        utente.setIdUser(4);
        utente.setUsername("juan");
        utente.setSaldo(500.0);

        Macchina audi = new Macchina();
        audi.setId(4);
        audi.setDisponibile(true);
        audi.setPrezzo(50.0);

        noleggio.setMacchina(audi);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        noleggioController.processaNoleggio(noleggio);

        assertTrue(true,"L'utente dovrebbe permettersi l'auto");

    }

    @Test
    void test_Utente_Con_Saldo_Insufficiente(){

        NoleggioAutoBean noleggio=new NoleggioAutoBean();

        Utente utente = new Utente();

        utente.setIdUser(4);
        utente.setUsername("juan");
        utente.setSaldo(10.0);

        Macchina audi = new Macchina();
        audi.setId(6);
        audi.setPrezzo(50.0);
        audi.setDisponibile(true);

        noleggio.setMacchina(audi);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(IllegalArgumentException.class,()->{noleggioController.processaNoleggio(noleggio);});
        assertEquals("Saldo insufficiente",ex.getMessage());
    }

    @Test
    void test_utente_null(){

        NoleggioAutoBean noleggio=new NoleggioAutoBean();

        Utente utente = null;

        Macchina macchina = new Macchina();

        macchina.setId(12);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(NullPointerException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });

        assertEquals("Cannot invoke \"model.utente.Utente.getSaldo()\" because \"utente\" is null",ex.getMessage());

    }

    @Test
    void test_auto_null(){

        NoleggioAutoBean noleggio=new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(4);
        utente.setSaldo(10.0);

        Macchina macchina = null;

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(NullPointerException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });

        assertEquals("Cannot invoke \"model.macchina.Macchina.getPrezzo()\" because \"auto\" is null",ex.getMessage());

    }

    @Test
    void giorni_noleggio_negativi(){

        NoleggioAutoBean noleggio=new NoleggioAutoBean();

        Utente utente = new Utente();

        utente.setIdUser(5);
        utente.setUsername("andy");
        utente.setSaldo(100.0);

        Macchina macchina = new Macchina();
        macchina.setId(12);
        macchina.setPrezzo(50.0);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(-1);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {noleggioController.processaNoleggio(noleggio);});

        assertEquals("Giorni non devono essere negativi.",ex.getMessage());
    }

    @Test
    void test_fine_noleggio_naturale() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(2);
        utente.setUsername("felo");
        utente.setSaldo(1000.0);

        Macchina macchina = new Macchina();
        macchina.setId(1);
        macchina.setPrezzo(20.0);
        macchina.setDisponibile(true);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(3);

        noleggioController.processaNoleggio(noleggio);

        List<NoleggioAuto> listaNoleggi = DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getRented();
        NoleggioAuto noleggioInMemoria = listaNoleggi.get(listaNoleggi.size() - 1);

        noleggioInMemoria.setDataFine(LocalDate.now().minusDays(1));
        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().sbloccaAutoScadute();

        assertEquals("Chiusura Naturale", noleggioInMemoria.getMotivoChiusura(), "Il noleggio nel sistema deve risultare CHIUSURA NATURALE");
    }

    @Test
    void test_fine_noleggio_anticipati() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(21);
        utente.setUsername("gallo");
        utente.setSaldo(1000.0);

        Macchina macchina = new Macchina();
        macchina.setId(17);
        macchina.setPrezzo(20.0);
        macchina.setDisponibile(true);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(3);

        noleggioController.processaNoleggio(noleggio);

        List<NoleggioAuto> listaNoleggiPrima = DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getRented();
        NoleggioAuto noleggioInMemoria = listaNoleggiPrima.get(listaNoleggiPrima.size() - 1);

        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().terminaNoleggio(noleggioInMemoria.getIdNoleggio(), "Chiusura Anticipata");

        List<NoleggioAuto> listaNoleggiDopo = DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getRented();
        NoleggioAuto noleggioAggiornato = listaNoleggiDopo.get(listaNoleggiDopo.size() - 1);

        assertEquals("Chiusura Anticipata", noleggioAggiornato.getMotivoChiusura(), "Il motivo della chiusura deve coincidere");

    }
}