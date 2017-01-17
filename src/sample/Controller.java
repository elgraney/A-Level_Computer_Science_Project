package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller extends BorderPane {

    @FXML private MenuBar menuBar;
    @FXML private Button importButton;
    @FXML public Label boom;
    @FXML private Button importTemplate;

    List<SImage> imagePool = new ArrayList<SImage>();


    public Controller() {
    }
    public void importImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image files");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.tif"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        System.out.println(selectedFiles);
        for (File selectedFile : selectedFiles) {
            SImage currentImage = new SImage(selectedFile);
            imagePool.add(currentImage);
        }
        for (SImage image : imagePool){
            System.out.println("image");

        }
    }
    public void importTemplate(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.tif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile);

        SImage templateImage = new SImage(selectedFile);


    }
    }

