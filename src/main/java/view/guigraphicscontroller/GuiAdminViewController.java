package view.guigraphicscontroller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import utils.SessionSingleton;
import utils.StageHandler;

import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class GuiAdminViewController {

    @FXML
    public void goToManage(ActionEvent event) throws IOException {
        String str= "/view/GestioneCatalogo.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void goToStorico(ActionEvent event) throws IOException {
        String str="/view/StoricoNoleggi.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }
    @FXML
    public void goToProfit(ActionEvent event) throws IOException {
        String str="/view/StoricoProfitti.fxml";
        StageHandler.getSingletonInstance().loadPage(str);
    }

    @FXML
    public void logOut(MouseEvent event) throws IOException {

        SessionSingleton.getInstance().logout();
        String str="/view/CatalogoView.fxml";
        StageHandler.getSingletonInstance().loadPage(str);

    }

}
