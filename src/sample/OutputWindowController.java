package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ma.crane on 20/02/2017.
 */
public class OutputWindowController {

    @FXML
    private ImageView outputFrame = null;
    @FXML
    private Button discardButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button printButton;

    private File outputFile;
    private String outputFormat;
    private Stage stage;

    public void innit(File outputFile, String outputFormat, Stage stage){
        this.outputFile = outputFile;
        this.outputFormat = outputFormat;
        this.stage = stage;
    }

    public void showOutputImage() {

        try {
            outputFrame.setImage(new Image(new FileInputStream(outputFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Specify a file to save");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(outputFile);
                ImageIO.write(image, outputFormat, file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

