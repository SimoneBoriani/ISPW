package GestioneCatalogo;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import model.macchina.Macchina;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.AperturaFileTEST;
import view.factory.ControllerFactory;


import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class TestGestioneCatalogo {

    private final GestioneCatalogoController gestioneCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    @BeforeAll
    static void setupDemoMode(){
        AperturaFileTEST.open();
    }

    @Test
    void test_Aggiunta_Auto_Corretta() {
        CatalogoBean bean = new CatalogoBean();
        bean.setMarca("Fiat");
        bean.setModello("Punto");
        bean.setAlimentazione("Benzina");
        bean.setTrasmissione("Manuale");
        bean.setTipologia("Utilitaria");
        bean.setFoto("punto.png");
        bean.setAnno(2010);
        bean.setPrezzo(20.0);
        bean.setPosti(5);

        assertDoesNotThrow(() -> {
            gestioneCatalogoController.validaEAggiungiAuto(bean);
        }, "L'inserimento di un'auto corretta non deve generare eccezioni");

        gestioneCatalogoController.confermaSalvataggio();

        List<Macchina> autoPresenti = gestioneCatalogoController.getCars();
        boolean autoTrovata = autoPresenti.stream()
                .anyMatch(m -> m.getModello().equals("Punto") && m.getMarca().equals("Fiat"));

        assertTrue(autoTrovata, "L'auto appena aggiunta deve essere presente nel catalogo");
    }

    @Test
    void test_Aggiunta_Auto_Marca_Mancante() {
        CatalogoBean bean = new CatalogoBean();
        bean.setMarca("");
        bean.setModello("Punto");
        bean.setAnno(2010);
        bean.setPrezzo(20.0);
        bean.setPosti(5);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            gestioneCatalogoController.validaEAggiungiAuto(bean);
        });

        assertEquals("Il campo Marca è obbligatorio.", ex.getMessage());
    }

    @Test
    void test_Aggiunta_Auto_Prezzo_Negativo() {
        CatalogoBean bean = new CatalogoBean();
        bean.setMarca("Fiat");
        bean.setModello("Punto");
        bean.setAnno(2010);
        bean.setPrezzo(-10.0);
        bean.setPosti(5);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            gestioneCatalogoController.validaEAggiungiAuto(bean);
        });

        assertEquals("Il prezzo deve essere maggiore di zero.", ex.getMessage());
    }

    @Test
    void test_Aggiunta_Auto_Posti_Non_Validi() {
        CatalogoBean bean = new CatalogoBean();
        bean.setMarca("Fiat");
        bean.setModello("Punto");
        bean.setAnno(2010);
        bean.setPrezzo(20.0);
        bean.setPosti(0);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            gestioneCatalogoController.validaEAggiungiAuto(bean);
        });

        assertEquals("Il numero di posti non è valido.", ex.getMessage());
    }

    @Test
    void test_Aggiunta_Auto_Anno_Non_Valido() {
        CatalogoBean bean = new CatalogoBean();
        bean.setMarca("Fiat");
        bean.setModello("Punto");
        bean.setAnno(1800);
        bean.setPrezzo(20.0);
        bean.setPosti(5);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            gestioneCatalogoController.validaEAggiungiAuto(bean);
        });

        assertEquals("Inserisci un anno valido.", ex.getMessage());
    }
}