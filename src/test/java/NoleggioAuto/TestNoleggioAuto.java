package NoleggioAuto;

import model.macchina.Macchina;
import model.utente.Utente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNoleggioAuto {

    @Test
    void testUtenteConSaldoSufficiente(){

        Utente utente = new Utente();

        utente.setUsername("juan");
        utente.setSaldo(500.0);

        Macchina audi = new Macchina();
        audi.setPrezzo(50.0);

        boolean esito = (utente.getSaldo()>=audi.getPrezzo());

        assertTrue(esito,"L'utente dovrebbe permettersi l'auto");

    }

    @Test
    void testUtenteConSaldoInsufficiente(){

        Utente utente = new Utente();

        utente.setUsername("juan");
        utente.setSaldo(10.0);

        Macchina audi = new Macchina();
        audi.setPrezzo(50.0);

        boolean esito = (utente.getSaldo()>=audi.getPrezzo());

        assertFalse(esito,"L'utente non dovrebbe permettersi l'auto");

    }
}