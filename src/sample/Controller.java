package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

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
    @FXML private ImageView imageFrame0;
    @FXML private ImageView imageFrame1;
    @FXML private ImageView imageFrame2;
    @FXML private ImageView imageFrame3;
    @FXML private ImageView imageFrame4;
    @FXML private ImageView imageFrame5;

    private List<SImage> SImagePool = new ArrayList<SImage>();
    private List<Image> imagePool = new ArrayList<Image>();

    private Integer page = 0;



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
            try {
                SImagePool.add(currentImage);
                imagePool.add(new Image(new FileInputStream(currentImage.file)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        updateImagePool(SImagePool, imagePool);
        }


    public void updateImagePool(List SImagePool, List imagePool){

        imageFrame0.setImage((Image) imagePool.get(0+page));
        imageFrame1.setImage((Image) imagePool.get(1+page));
        imageFrame2.setImage((Image) imagePool.get(2+page));
        imageFrame3.setImage((Image) imagePool.get(3+page));

    }
    public void importTemplate(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.tif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile);

        SImage templateImage = new SImage(selectedFile);


    }
    }

