package start;

import javafx.application.Application;
//import view.factory.CliGraphicsFactory;
import view.factory.GuiGraphicsFactory;

import java.util.Scanner;

public class Launcher {
    public static void main(String[] args) {

        boolean bool;

        System.out.println("Selezionare 1-GUI , 2-CLI");
        Scanner scanner = new Scanner(System.in);
        int sc=scanner.nextInt();

        if (sc == 1 || sc == 2) {
            bool = sc == 1;

            if (bool) {
                Application.launch(GuiGraphicsFactory.class, args);
            } else {
                System.out.println("Questa è l'interfaccia a linea di comando (Da implementare)");
                //Application.launch(CliGraphicsFactory.class, args);
            }
        }
        else{
            System.out.println("Numero non valido");

        }
    }
}