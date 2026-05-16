package view.cligraphicscontroller;

import bean.CatalogoBean;
import controller.GestioneCatalogoController;
import model.macchina.Macchina;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.ArrayList;
import java.util.List;

public class CliGestioneCatalogoPage {

    private final GestioneCatalogoController gestioneCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createGestioneCatalogoController();

    private static final String CMD_1 = "A";
    private static final String CMD_2 = "M";
    private static final String CMD_3 = "D";
    private static final String CMD_4 = "B";
    private static final String MSG_MOD_AUTO = "Modifica parametri auto";
    private static final String LIST_FORMAT = "%-4s | %-12s | %-12s | %-8s%n";

    private List<Macchina> autoTrovate = new ArrayList<>();
    private Macchina autoSelezionata;

    public void render() {
        boolean back = false;
        this.autoTrovate = gestioneCatalogoController.getCars();
        ConsolePrinter.printHeader("Gestione Catalogo");

        while (!back) {
            mostraListaAuto();

            ConsolePrinter.printMenuOption(CMD_1, "Aggiungi auto al catalogo");
            ConsolePrinter.printMenuOption(CMD_4, "Torna indietro");

            String choice = ConsolePrinter.readLine("Selezione (id auto per aprire le impostazioni) > ").trim().toUpperCase();

            if (CMD_1.equals(choice)) {
                addCar();
                this.autoTrovate = gestioneCatalogoController.getCars();
            } else if (CMD_4.equals(choice)) {
                back = true;
            } else if (!choice.isEmpty()) {
                gestisciSottomenuOpzioni(choice);
            }
        }
    }

    private void gestisciSottomenuOpzioni(String choice) {
        gestisciSelezioneAuto(choice);
        this.autoSelezionata = SessionSingleton.getInstance().getAutoSelezionata();

        if (this.autoSelezionata != null) {
            ConsolePrinter.printHeader("Opzioni modifica auto");
            mostraListaAuto();

            ConsolePrinter.printMenuOption(CMD_2, MSG_MOD_AUTO);
            ConsolePrinter.printMenuOption(CMD_3, "Elimina auto");

            String subChoice = ConsolePrinter.readLine("Selezione (Premere ENTER per tornare indietro)> ").trim().toUpperCase();

            if (CMD_2.equals(subChoice)) {
                modifyCar();
                this.autoTrovate = gestioneCatalogoController.getCars();
            } else if (CMD_3.equals(subChoice)) {
                deleteCar();
                this.autoTrovate = gestioneCatalogoController.getCars();
            }

            this.autoSelezionata = null;
            SessionSingleton.getInstance().setAutoSelezionata(null);
        }
    }

    private void addCar() {
        ConsolePrinter.printHeader("Aggiungi auto");
        CatalogoBean bean = new CatalogoBean();
        riempiBean(bean);
        gestioneCatalogoController.salvaAutoRam(bean);
        gestioneCatalogoController.confermaSalvataggio();
    }

    private void modifyCar() {
        ConsolePrinter.printHeader(MSG_MOD_AUTO);
        CatalogoBean bean = new CatalogoBean();
        bean.setId(this.autoSelezionata.getId());
        riempiBean(bean);
        gestioneCatalogoController.modifyCar(bean);
    }

    private void riempiBean(CatalogoBean bean) {

        bean.setModello(ConsolePrinter.readLine("Inserisci il modello:").trim());
        bean.setMarca(ConsolePrinter.readLine("Inserisci la marca:").trim());
        bean.setAlimentazione(ConsolePrinter.readLine("Inserisci l'alimentazione:").trim());
        bean.setTrasmissione(ConsolePrinter.readLine("Inserisci la trasmissione:").trim());
        bean.setAnno(Integer.parseInt(ConsolePrinter.readLine("Inserisci l'anno:").trim()));
        bean.setPosti(Integer.parseInt(ConsolePrinter.readLine("Inserisci i posti:").trim()));
        bean.setPrezzo(Double.parseDouble(ConsolePrinter.readLine("Inserisci  il prezzo:").trim()));
        bean.setTipologia(ConsolePrinter.readLine("Inserisci la tipologia:").trim());
        bean.setFoto(ConsolePrinter.readLine("Inserisci url della foto:").trim());

    }

    private void deleteCar() {

        CatalogoBean bean = new CatalogoBean();
        bean.setId(this.autoSelezionata.getId());
        gestioneCatalogoController.removeCar(bean);

    }

    private void gestisciSelezioneAuto(String id) {

        try {
            List<Macchina> catalogo = gestioneCatalogoController.getCars();
            Macchina selezionata = catalogo.stream()
                    .filter(m -> String.valueOf(m.getId()).equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (selezionata != null) {
                SessionSingleton.getInstance().setAutoSelezionata(selezionata);
                ConsolePrinter.printStatus("Auto selezionata: " + selezionata.getModello(), false);
            } else {
                ConsolePrinter.printStatus("ID non valido.", true);
                SessionSingleton.getInstance().setAutoSelezionata(null);
            }
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore selezione.", true);
            SessionSingleton.getInstance().setAutoSelezionata(null);
        }
    }

    private void mostraListaAuto() {
        if (this.autoTrovate == null || this.autoTrovate.isEmpty()) {
            ConsolePrinter.printStatus("Nessuna auto disponibile.", true);
            return;
        }

        ConsolePrinter.logFormatted("%n" + LIST_FORMAT, "ID", "MARCA", "MODELLO", "PREZZO");
        ConsolePrinter.logFormatted("-".repeat(46) + "%n");

        if (this.autoSelezionata != null) {
            ConsolePrinter.logFormatted(LIST_FORMAT,
                    this.autoSelezionata.getId(),
                    this.autoSelezionata.getMarca(),
                    this.autoSelezionata.getModello(),
                    this.autoSelezionata.getPrezzo() + "€");
        } else {
            for (Macchina m : this.autoTrovate) {
                ConsolePrinter.logFormatted(LIST_FORMAT,
                        m.getId(),
                        m.getMarca(),
                        m.getModello(),
                        m.getPrezzo() + "€");
            }
        }
    }
}