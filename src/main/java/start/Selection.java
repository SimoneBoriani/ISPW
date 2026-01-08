package start;

import javafx.application.Application;
import view.factory.GuiGraphicsFactory;

import java.util.Scanner;

public class Selection {
    public void loader(String args) {

        boolean bool,condizione=true;
        System.out.println("Selezionare: 1-GUI  2-CLI");

        while(condizione){

        Scanner scanner = new Scanner(System.in);
        int sc =1; //scanner.nextInt();

        if (sc == 1 || sc == 2) {
            bool = sc == 1;

            if (bool) {
                condizione = false;
                Application.launch(GuiGraphicsFactory.class, args);
            } else {
                System.out.println("Questa è l'interfaccia a linea di comando (Da implementare)");
                condizione = false;
                //Application.launch(CliGraphicsFactory.class, args);
            }
        } else {
            System.out.println("Numero non valido, riprovare inserendo 1 o 2");
        }
    }
    }
}