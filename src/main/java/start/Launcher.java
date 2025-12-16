package start;

import javafx.application.Application;
import view.factory.GuiGraphicsFactory;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(GuiGraphicsFactory.class, args);
    }
}
