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
//This class controls the running of the output window
public class OutputWindowController {
    //All @FXML variable are objects within the output window that are accessed at some point in the program
    @FXML private ImageView outputFrame = null;
    @FXML private Button discardButton;
    @FXML private Button saveButton;
    @FXML private Button sendButton;
    @FXML private Button printButton;
    @FXML private Label resLabel;
    @FXML private Label noSectionsLabel;
    @FXML private Label noUniqueImagesLabel;

    private File outputFile;
    private String outputFormat;
    private Stage stage;
    private int sectionCount;
    private int uniqueImageCount;
    private double width;
    private double height;


    //the controller is initialised with a set of necessary values
    public void innit(File outputFile, String outputFormat, Stage stage, int sectionCount, int uniqueImageCount) {

        //print and send are not currently implemented
        printButton.setDisable(true);
        sendButton.setDisable(true);
        this.outputFile = outputFile;
        this.outputFormat = outputFormat;
        this.stage = stage;

        //metadata to display to the user
        this.sectionCount = sectionCount;
        this.uniqueImageCount = uniqueImageCount;

        //loads the output file
        Image image = null;
        try {
            image = new Image(new FileInputStream(outputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //more metadata
        width = image.getWidth();
        height = image.getHeight();
        //displays the metadata
        resLabel.setText(Long.toString(Math.round(width))+"x"+Long.toString(Math.round(height)));
        noSectionsLabel.setText(Integer.toString(sectionCount));
        noUniqueImagesLabel.setText(Integer.toString(uniqueImageCount));
    }

    //the output image is displayed in an imageFrame
    public void showOutputImage() {
        try {
            outputFrame.setImage(new Image(new FileInputStream(outputFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //triggered when the "Save" button is pressed
    public void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Specify a file to save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*"+outputFormat));
        //the user names the file in the dialog
        File file = fileChooser.showSaveDialog(stage);
        String fileString = file.toString();
        //if the user does not write ".jpg" or ".png" (or whatever is appropriate for the file) then it is appended to the string
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
    //the window is closed and the image is not saved.
    public void discard(){
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure? This window will close and all unsaved progress will be lost", "Discard?", dialogButton);
        if(dialogResult == 0) {
            stage.close();
        }


    }
}

