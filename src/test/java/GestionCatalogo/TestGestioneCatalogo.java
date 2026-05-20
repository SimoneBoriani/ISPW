package GestionCatalogo;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import model.macchina.Macchina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.AperturaFileTEST;
import view.factory.ControllerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestGestioneCatalogo {

    private GestioneCatalogoController gestioneCatalogoController;

    @BeforeEach
    void setUp(){
        AperturaFileTEST.open();
        gestioneCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();
    }

    @Test
    void aggiungi_auto_successo() {

        CatalogoBean aggiungi = new CatalogoBean();
        aggiungi.setMarca("Audi");
        aggiungi.setModello("Rs3");
        aggiungi.setPrezzo(10.0);

        gestioneCatalogoController.salvaAutoRam(aggiungi);
        gestioneCatalogoController.confermaSalvataggio();

        List<Macchina> catalogoAttuale = gestioneCatalogoController.getCars();

        assertEquals(1, catalogoAttuale.size(), "Il catalogo dovrebbe contenere esattamente 1 auto");
    }

    @Test
    void aggiungi_auto_null_oggetto() {

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            gestioneCatalogoController.salvaAutoRam(null);
        });
        assertEquals("L'oggetto passato non può essere null.", ex.getMessage());
    }

    @Test
    void aggiungi_auto_modello_null(){

        CatalogoBean nuovaAuto = new CatalogoBean();

        Macchina auto = new Macchina();

        auto.setModello(null);
        auto.setMarca("audi");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {gestioneCatalogoController.salvaAutoRam(nuovaAuto);});

        assertEquals("Campi modello e marca devono essere not null.",ex.getMessage());
    }

    @Test
    void modifica_auto_rendendola_null(){

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            gestioneCatalogoController.modifyCar(null);
        });

        assertEquals("L'oggetto passato non può essere null.",ex.getMessage());
    }

    @Test
    void modifica_auto_modello_null(){

        CatalogoBean nuovaAuto = new CatalogoBean();

        Macchina auto = new Macchina();

        auto.setModello(null);
        auto.setMarca("toyota");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {gestioneCatalogoController.salvaAutoRam(nuovaAuto);});

        assertEquals("Campi modello e marca devono essere not null.",ex.getMessage());
    }
}