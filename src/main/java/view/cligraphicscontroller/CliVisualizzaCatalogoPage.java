package view.cligraphicscontroller;

import bean.CatalogoBean;
import controller.VisualizzaCatalogoController;
import exceptions.CarNotFoundException;
import model.macchina.Macchina;
import utils.ConsolePrinter;
import utils.SessionSingleton;
import view.factory.ControllerFactory;

import java.util.ArrayList;
import java.util.List;

public class CliVisualizzaCatalogoPage {

    private final VisualizzaCatalogoController visualizzaCatalogoController = ControllerFactory.getGraphicalSingletonFactory().createVisualizzaCatalogoController();

    public void render() {

        boolean back = false;

        mostraListaAuto(visualizzaCatalogoController.getCars());

        while (!back) {



            ConsolePrinter.printMenuOption("R", "Ricerca (Filtri)");
            ConsolePrinter.printMenuOption("REF", "Aggiorna Catalogo");
            ConsolePrinter.printMenuOption("0", "Torna Indietro");

            String choice = ConsolePrinter.readLine("Scelta (ID per Noleggio o Lettera) > ").toUpperCase();

            switch (choice) {
                case "R" -> apriRicerca();
                case "REF" -> { /*Riavvia*/}
                case "0" -> back = true;
                default -> gestisciSelezioneAuto(choice);
            }
        }
    }

    private void mostraListaAuto(List<Macchina> listaAuto) {

        if (listaAuto == null || listaAuto.isEmpty()) {
            ConsolePrinter.printStatus("Nessuna auto disponibile.", true);
            return;
        }

        String format = "%-4s | %-12s | %-12s | %-8s%n";
        ConsolePrinter.logFormatted("%n" + format, "ID", "MARCA", "MODELLO", "PREZZO");
        ConsolePrinter.logFormatted("-".repeat(46) + "%n");

        for (Macchina m : listaAuto) {
            ConsolePrinter.logFormatted(format,
                    m.getId(),
                    m.getMarca(),
                    m.getModello(),
                    m.getPrezzo() + "€");
        }
    }

    private void gestisciSelezioneAuto(String id) {
        try {

            List<Macchina> catalogo = visualizzaCatalogoController.getCars();
            Macchina selezionata = catalogo.stream()
                    .filter(m -> String.valueOf(m.getId()).equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);

            if (selezionata != null) {
                SessionSingleton.getInstance().setAutoSelezionata(selezionata);
                ConsolePrinter.printStatus("Auto selezionata: " + selezionata.getModello(), false);
                 new CliNoleggioPage().render();
            } else {
                ConsolePrinter.printStatus("ID non valido.", true);
            }
        } catch (Exception e) {
            ConsolePrinter.printStatus("Errore selezione.", true);
        }
    }

    private void apriRicerca() {

        ConsolePrinter.printHeader("Filtri di Ricerca");
        CatalogoBean bean = new CatalogoBean();

        String marca = ConsolePrinter.readLine("Marca (Invio per saltare):");
        if (!marca.isBlank()) bean.setMarca(marca);

        String modello = ConsolePrinter.readLine("Modello (Invio per saltare):");
        if (!modello.isBlank()) bean.setModello(modello);

        String prezzo = ConsolePrinter.readLine("Prezzo Max (Invio per saltare):");
        if (!prezzo.isBlank()) {
            try { bean.setPrezzo(Integer.parseInt(prezzo)); }
            catch (NumberFormatException e) { ConsolePrinter.printStatus("Prezzo non valido", true); }
        }

        ConsolePrinter.printStatus("Ricerca in corso...", false);

        List<Macchina> trovate = new ArrayList<>();

        try {

            ConsolePrinter.printStatus("Auto trovate:",false);

            trovate = visualizzaCatalogoController.research(bean);
            mostraListaAuto(trovate);

            ConsolePrinter.printMenuOption("N","Nuova ricerca");
            ConsolePrinter.printMenuOption("B","Torna Indietro");
            ConsolePrinter.printMenuOption("REF","Aggiorna");


            String choice1 = ConsolePrinter.readLine("Scelta (ID per Noleggio o Lettera) > ").toUpperCase();

            switch (choice1) {

                case "N" -> apriRicerca();
                case "B" -> render();
                case "REF" -> { /* Il loop ricarica automaticamente */ }
                default -> gestisciSelezioneAuto(choice1) ;

            }

        } catch (CarNotFoundException e) {
            mostraListaAuto(trovate);
        }
    }
}