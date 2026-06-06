package NoleggioAuto;

import bean.NoleggioAutoBean;
import controller.NoleggioController;
import exceptions.DocsNotValidException;
import model.daofactory.DaoFactory;
import model.macchina.Macchina;
import model.noleggioauto.NoleggioAuto;
import model.utente.Utente;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.AperturaFileTEST;
import view.factory.ControllerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestNoleggioAuto {

    private final NoleggioController noleggioController=ControllerFactory.getGraphicalSingletonFactory().createNoleggioController();

    @BeforeAll
    static void setupDemoMode(){
        AperturaFileTEST.open();
    }


    @Test
    void test_Utente_Con_Saldo_Sufficiente() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(1);
        utente.setUsername("juan");
        utente.setSaldo(500.0);
        utente.setVerificato(true);

        Macchina audi = new Macchina();
        audi.setId(1);
        audi.setDisponibile(true);
        audi.setPrezzo(50.0);

        noleggio.setMacchina(audi);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        assertDoesNotThrow(() -> {
            noleggioController.processaNoleggio(noleggio);
        }, "Il noleggio non dovrebbe lanciare eccezioni in DEMO mode");
    }

    @Test
    void test_Utente_Con_Saldo_Insufficiente() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(2);
        utente.setUsername("juan");
        utente.setSaldo(10.0);
        utente.setVerificato(true);

        Macchina audi = new Macchina();
        audi.setId(2);
        audi.setPrezzo(50.0);
        audi.setDisponibile(true);

        noleggio.setMacchina(audi);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });
        assertEquals("Saldo insufficiente", ex.getMessage());
    }

    @Test
    void test_Patente_Non_Verificata() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(3);
        utente.setUsername("juan");
        utente.setSaldo(500.0);
        utente.setVerificato(false);

        Macchina audi = new Macchina();
        audi.setId(3);
        audi.setPrezzo(50.0);
        audi.setDisponibile(true);

        noleggio.setMacchina(audi);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(DocsNotValidException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });
        assertEquals("Impossibile noleggiare: Patente non verificata.", ex.getMessage());
    }

    @Test
    void test_utente_null() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = null;

        Macchina macchina = new Macchina();
        macchina.setId(12);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(NullPointerException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });

        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void test_auto_null() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(4);
        utente.setSaldo(10.0);
        utente.setVerificato(true);

        Macchina macchina = null;

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        Exception ex = assertThrows(NullPointerException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });

        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void giorni_noleggio_negativi() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(5);
        utente.setUsername("andy");
        utente.setSaldo(100.0);
        utente.setVerificato(true);

        Macchina macchina = new Macchina();
        macchina.setId(12);
        macchina.setPrezzo(50.0);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(-1);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            noleggioController.processaNoleggio(noleggio);
        });

        assertEquals("Giorni non devono essere negativi.", ex.getMessage());
    }

    @Test
    void test_fine_noleggio_anticipati() {

        NoleggioAutoBean noleggio = new NoleggioAutoBean();

        Utente utente = new Utente();
        utente.setIdUser(21);
        utente.setUsername("gallo");
        utente.setSaldo(1000.0);
        utente.setVerificato(true);

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
        assertEquals("TERMINATO", noleggioAggiornato.getStato(), "Lo stato deve essere aggiornato a TERMINATO");
    }
    @Test
    void test_fine_noleggio_naturale() {
        NoleggioAutoBean noleggio = new NoleggioAutoBean();
        Utente utente = new Utente();
        utente.setIdUser(22);
        utente.setSaldo(1000.0);
        utente.setVerificato(true);

        Macchina macchina = new Macchina();
        macchina.setId(18);
        macchina.setPrezzo(10.0);
        macchina.setDisponibile(true);

        noleggio.setMacchina(macchina);
        noleggio.setRenter(utente);
        noleggio.setGiorni(1);

        noleggioController.processaNoleggio(noleggio);

        List<NoleggioAuto> lista = DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getRented();
        NoleggioAuto noleggioCreato = lista.get(lista.size() - 1);

        noleggioCreato.setDataFine(java.time.LocalDate.now().minusDays(1));

        DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().sbloccaAutoScadute();

        List<NoleggioAuto> listaDopo = DaoFactory.getDaoSingletonFactory().createNoleggioAutoDao().getRented();
        NoleggioAuto noleggioAggiornato = listaDopo.stream()
                .filter(n -> n.getIdNoleggio() == noleggioCreato.getIdNoleggio())
                .findFirst()
                .orElseThrow();

        assertEquals("Chiusura Naturale", noleggioAggiornato.getMotivoChiusura(), "Il motivo deve essere Chiusura Naturale");
        assertEquals("TERMINATO", noleggioAggiornato.getStato(), "Lo stato deve essere TERMINATO");
        assertTrue(noleggioAggiornato.getMacchina().getDisponibile(), "L'auto dovrebbe essere tornata disponibile");
    }
}