package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Controller extends BorderPane {

    @FXML private MenuBar menuBar;
    @FXML private Button importButton;
    @FXML public Label boom;

    public Controller() {
    }
    public void importImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.tif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile);
        try {
            SImage importedImage = new SImage();

        }
        catch(IOException e) {
            return;
        }


        //open single and multi image from windows using filechooser
        //then kick off SImage.analyse with on that item.
    }
    }

