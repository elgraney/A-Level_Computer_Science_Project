package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class Controller extends BorderPane {

    @FXML private MenuBar menuBar;

    public Controller() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "Application.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
