package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by ma.crane on 20/02/2017.
 */
public class OutputWindowController{

    @FXML private ImageView outputFrame = null;
    @FXML private Button discardButton;
    @FXML private Button saveButton;
    @FXML private Button sendButton;
    @FXML private Button printButton;

    public void showOutputImage(File outputFile){
        try {
            outputFrame.setImage(new Image(new FileInputStream(outputFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

