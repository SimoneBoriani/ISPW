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
    private static final String INVIO = "Premi INVIO per continuare...";

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
        try {
            CatalogoBean bean = new CatalogoBean();
            riempiBean(bean, null);
            gestioneCatalogoController.salvaAutoRam(bean);
            gestioneCatalogoController.confermaSalvataggio();
            ConsolePrinter.printStatus("Auto aggiunta con successo al catalogo!", false);
        } catch (NumberFormatException e) {
            ConsolePrinter.printStatus("Errore di inserimento: I campi Anno, Posti e Prezzo devono contenere solo numeri validi.", true);
        } catch (IllegalArgumentException e) {
            ConsolePrinter.printStatus("Errore di validazione: " + e.getMessage(), true);
        } catch (Exception e) {
            ConsolePrinter.printStatus("Si è verificato un errore di sistema: " + e.getMessage(), true);
        }
        ConsolePrinter.readLine(INVIO);
    }

    private void modifyCar() {
        ConsolePrinter.printHeader(MSG_MOD_AUTO);
        ConsolePrinter.logFormatted("Premi INVIO per mantenere il valore attuale");
        try {
            CatalogoBean bean = new CatalogoBean();
            bean.setId(this.autoSelezionata.getId());
            riempiBean(bean, this.autoSelezionata);
            gestioneCatalogoController.modifyCar(bean);
            ConsolePrinter.printStatus("Auto modificata con successo!", false);
        } catch (NumberFormatException e) {
            ConsolePrinter.printStatus("Errore di inserimento: I campi Anno, Posti e Prezzo devono contenere solo numeri validi.", true);
        } catch (IllegalArgumentException e) {
            ConsolePrinter.printStatus("Errore di validazione: " + e.getMessage(), true);
        } catch (Exception e) {
            ConsolePrinter.printStatus("Si è verificato un errore di sistema: " + e.getMessage(), true);
        }
        ConsolePrinter.readLine(INVIO);
    }

    private void riempiBean(CatalogoBean bean, Macchina oldAuto) {
        bean.setModello(leggiStringa("Inserisci il modello", oldAuto != null ? oldAuto.getModello() : ""));
        bean.setMarca(leggiStringa("Inserisci la marca", oldAuto != null ? oldAuto.getMarca() : ""));
        bean.setAlimentazione(leggiStringa("Inserisci l'alimentazione", oldAuto != null ? oldAuto.getAlimentazione() : ""));
        bean.setTrasmissione(leggiStringa("Inserisci la trasmissione", oldAuto != null ? oldAuto.getTrasmissione() : ""));
        bean.setTipologia(leggiStringa("Inserisci la tipologia", oldAuto != null ? oldAuto.getTipologia() : ""));
        bean.setFoto(leggiStringa("Inserisci url della foto", oldAuto != null ? oldAuto.getImageUrl() : ""));

        bean.setAnno((int) leggiNumero("Inserisci l'anno (solo numeri)", oldAuto != null ? oldAuto.getAnno() : 0));
        bean.setPosti((int) leggiNumero("Inserisci i posti (solo numeri)", oldAuto != null ? oldAuto.getPosti() : 0));
        bean.setPrezzo(leggiNumero("Inserisci il prezzo (es. 50.5)", oldAuto != null ? oldAuto.getPrezzo() : 0.0));
    }

    private String leggiStringa(String prompt, String oldValue) {
        String displayPrompt = oldValue.isEmpty() ? prompt + ": " : prompt + " [" + oldValue + "]: ";
        String input = ConsolePrinter.readLine(displayPrompt).trim();
        return input.isEmpty() ? oldValue : input;
    }

    private double leggiNumero(String prompt, double oldValue) {
        String displayPrompt = oldValue == 0 ? prompt + ": " : prompt + " [" + oldValue + "]: ";
        String input = ConsolePrinter.readLine(displayPrompt).trim();
        if (input.isEmpty()) {
            return oldValue;
        }
        return Double.parseDouble(input);
    }

    private void deleteCar() {
        try {
            CatalogoBean bean = new CatalogoBean();
            bean.setId(this.autoSelezionata.getId());
            gestioneCatalogoController.removeCar(bean);
            ConsolePrinter.printStatus("Auto eliminata con successo!", false);
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore durante l'eliminazione: " + e.getMessage(), true);
        }
        ConsolePrinter.readLine(INVIO);
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
                ConsolePrinter.readLine(INVIO);
            }
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore durante la selezione: " + e.getMessage(), true);
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