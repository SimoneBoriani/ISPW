package view.cligraphicscontroller;

import utils.ConsolePrinter;
import utils.SessionSingleton;

public class CliAdminPage {

    public void render(){

        ConsolePrinter.printHeader("Area Admin");
        boolean back=false;

        while(!back){

            ConsolePrinter.printMenuOption("1","Gestione Catalogo");
            ConsolePrinter.printMenuOption("2","Visalizza Storico Noleggi");
            ConsolePrinter.printMenuOption("3","Visualizza Storico Profitti");
            ConsolePrinter.printMenuOption("0","Log out");

            String choice = ConsolePrinter.readLine("Scelta >").trim();

            switch(choice) {
                case "1" -> goToManage();
                case "2" -> goToRecord();
                case "3" -> goToProfit();
                case "0" -> {

                    SessionSingleton.getInstance().logout();
                    ConsolePrinter.printStatus("Logout effettuato con successo.", false);
                    back = true;
                }
                default -> ConsolePrinter.printStatus("Scelta non valida!", true);
            }

        }
    }
    private void goToManage(){
        new CliGestioneCatalogoPage().render();
    }
    private void goToRecord(){
        new CliVisualizzaStoricoPage().renderStorico();
    }
    private void goToProfit(){
        new CliVisualizzaStoricoPage().renderProfitti();
    }
}
