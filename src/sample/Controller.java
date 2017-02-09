package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.*;

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


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;

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

    @FXML private ImageView imageFrame6;
    @FXML private ImageView imageFrame7;
    @FXML private ImageView imageFrame8;
    @FXML private ImageView imageFrame9;
    @FXML private ImageView imageFrame10;
    @FXML private ImageView imageFrame11;

    @FXML private ImageView imageFrame12;
    @FXML private ImageView imageFrame13;
    @FXML private ImageView imageFrame14;
    @FXML private ImageView imageFrame15;
    @FXML private ImageView imageFrame16;
    @FXML private ImageView imageFrame17;

    @FXML private ImageView imageFrame18;
    @FXML private ImageView imageFrame19;
    @FXML private ImageView imageFrame20;
    @FXML private ImageView imageFrame21;
    @FXML private ImageView imageFrame22;
    @FXML private ImageView imageFrame23;

    @FXML private ImageView templateFrame;

    @FXML private  Label pageLabel;

    //outputwindow
    @FXML private ImageView outputFrame;

    private ImageView[] imageFrameList;
    private ArrayList<SImage> SImagePool = new ArrayList<SImage>();
    private ArrayList<Image> imagePool = new ArrayList<Image>();
    private SImage templateSImage;
    private BufferedImage templateImage;


    private Integer page = 0;


    public void innit(Stage primaryStage) {
        imageFrameList = new ImageView[]{imageFrame0, imageFrame1, imageFrame2, imageFrame3, imageFrame4, imageFrame5, imageFrame6, imageFrame7, imageFrame8, imageFrame9, imageFrame10, imageFrame11, imageFrame12, imageFrame13, imageFrame14, imageFrame15, imageFrame16, imageFrame17, imageFrame18, imageFrame19, imageFrame20, imageFrame21, imageFrame22, imageFrame23};
        System.out.println(imageFrame23);
        pageLabel.setText(Integer.toString(page + 1));
    }

    public void beginGenerationPhase(){
        Pane popupPane = new Pane();
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneratePopUp.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        try {
            popupPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(popupPane);
        stage.setScene(scene);
        stage.setTitle("My modal window");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        //make editable.
        int analysisLevel = 3;
        ImageFactory.generate( templateSImage, SImagePool, templateImage, analysisLevel);

        Pane outputPane = new Pane();
        Stage outputStage = new Stage();
        FXMLLoader outputLoader = new FXMLLoader(getClass().getResource("outputWindow.fxml"));
        Controller outputController = new Controller();
        loader.setController(outputController);

        try {
            outputPane = outputLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene outputScene = new Scene(outputPane);
        outputStage.setScene(outputScene);
        outputStage.setTitle("My modal window");
        outputStage.initModality(Modality.APPLICATION_MODAL);
        outputStage.showAndWait();
    }
    public void importImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image files");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        System.out.println(selectedFiles);
        for (File selectedFile : selectedFiles) {
            SImage currentImage = new SImage(selectedFile, 3);
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
        clearPool();
        pageLabel.setText(Integer.toString(page+1));
        int length =  imagePool.size();
        int startIndex = (page * 24);
        int endIndex;

        if (length>(page + 1)*24-1){
            endIndex = (page+1)*24 -1;
        }
        else{
            endIndex = (page * 24) + (length % 24) - 1;
        }
        //System.out.println("start "+ startIndex);
        //System.out.println("end "+endIndex);
        //System.out.println("Lenght "+length);
        for (int index = startIndex; index <= endIndex; index++){
            //System.out.println("Mod: " + (index % 24));
            imageFrameList[(index % 24)].setImage( (Image) imagePool.get(index));
        }
        }



    public void importTemplate(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile);


        templateSImage = new SImage(selectedFile, 2);

        try {
            templateFrame.setImage(new Image(new FileInputStream(templateSImage.file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            templateImage = ImageIO.read(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void nextPage(){
        if (((page+1)*24)<= imagePool.size()){
            page = page +1;
            updateImagePool(SImagePool, imagePool);
        }
        else{
            System.out.print("no next page");
        }
    }
    public void previousPage(){
        if(page > 0) {
            page = page - 1;

            updateImagePool(SImagePool, imagePool);
        }
        else{
            System.out.println("No previous page");
        }

    }
    private void clearPool(){
        for (ImageView frame: imageFrameList){
            frame.setImage(null);
        }
    }
}

