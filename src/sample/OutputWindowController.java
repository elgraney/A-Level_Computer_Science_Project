package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    @FXML
    private Label resLabel;
    @FXML
    private Label noSectionsLabel;
    @FXML
    private Label noUniqueImagesLabel;

    private File outputFile;
    private String outputFormat;
    private Stage stage;
    private int sectionCount;
    private int uniqueImageCount;
    private double width;
    private double height;

    public void innit(File outputFile, String outputFormat, Stage stage, int sectionCount, int uniqueImageCount) {
        this.outputFile = outputFile;
        this.outputFormat = outputFormat;
        this.stage = stage;
        this.sectionCount = sectionCount;
        this.uniqueImageCount = uniqueImageCount;
        Image image = null;
        try {
            image = new Image(new FileInputStream(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        width = image.getWidth();
        height = image.getHeight();

        resLabel.setText(Long.toString(Math.round(width))+"x"+Long.toString(Math.round(height)));
        noSectionsLabel.setText(Integer.toString(sectionCount));
        noUniqueImagesLabel.setText(Integer.toString(uniqueImageCount));


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
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*"+outputFormat));
        File file = fileChooser.showSaveDialog(stage);
        System.out.println(file);
        String fileString = file.toString();
        System.out.println(fileString);
        if(fileString.length()>=4) {
            if (fileString.substring(fileString.length() - 3) != "." + outputFormat) {
                file = new File(fileString + "." + outputFormat);
            }
        }
        else{
            file = new File(fileString + "." + outputFormat);
        }
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

