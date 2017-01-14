package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception{
        BorderPane pane;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Application.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Title");
        stage.show();
    }

}