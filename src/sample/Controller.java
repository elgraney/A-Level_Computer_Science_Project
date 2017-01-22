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

    private List<SImage> SImagePool = new ArrayList<SImage>();
    private List<Image> imagePool = new ArrayList<Image>();

    private Integer page = 0;



    public Controller() {
    }
    public void importImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image files");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        System.out.println(selectedFiles);
        for (File selectedFile : selectedFiles) {
            SImage currentImage = new SImage(selectedFile);
            try {
                SImagePool.add(currentImage);
                imagePool.add(new Image(new FileInputStream(currentImage.file)));
                updateImagePool(SImagePool, imagePool);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        }


    public void updateImagePool(List SImagePool, List imagePool){
        int length =  imagePool.size();
        if(length>=1) {
            imageFrame0.setImage((Image) imagePool.get(0 + page));
            if(length>=2) {
                imageFrame1.setImage((Image) imagePool.get(1 + page));
                if(length>=3) {
                    imageFrame2.setImage((Image) imagePool.get(2 + page));
                    if(length>=4) {
                        imageFrame3.setImage((Image) imagePool.get(3 + page));
                        if(length>=5) {
                            imageFrame4.setImage((Image) imagePool.get(4 + page));
                            if(length>=6) {
                                imageFrame5.setImage((Image) imagePool.get(5 + page));
                                if(length>=7) {
                                    imageFrame6.setImage((Image) imagePool.get(6 + page));
                                    if(length>=8) {
                                        imageFrame7.setImage((Image) imagePool.get(7 + page));
                                        if(length>=9) {
                                            imageFrame8.setImage((Image) imagePool.get(8 + page));
                                            if(length>=10) {
                                                imageFrame9.setImage((Image) imagePool.get(9 + page));
                                                if(length>=11) {
                                                    imageFrame10.setImage((Image) imagePool.get(10 + page));
                                                    if(length>=12) {
                                                        imageFrame11.setImage((Image) imagePool.get(11 + page));
                                                        if(length>=13) {
                                                            imageFrame12.setImage((Image) imagePool.get(12 + page));
                                                            if(length>=14) {
                                                                imageFrame13.setImage((Image) imagePool.get(13 + page));
                                                                if(length>=15) {
                                                                    imageFrame14.setImage((Image) imagePool.get(14 + page));
                                                                    if(length>=16) {
                                                                        imageFrame15.setImage((Image) imagePool.get(15 + page));
                                                                        if(length>=17) {
                                                                            imageFrame16.setImage((Image) imagePool.get(16 + page));
                                                                            if(length>=18) {
                                                                                imageFrame17.setImage((Image) imagePool.get(17 + page));
                                                                                if(length>=19) {
                                                                                    imageFrame18.setImage((Image) imagePool.get(18 + page));
                                                                                    if(length>=20) {
                                                                                        imageFrame19.setImage((Image) imagePool.get(19 + page));
                                                                                        if(length>=21) {
                                                                                            imageFrame20.setImage((Image) imagePool.get(20 + page));
                                                                                            if(length>=22) {
                                                                                                imageFrame21.setImage((Image) imagePool.get(21 + page));
                                                                                                if(length>=23) {
                                                                                                    imageFrame22.setImage((Image) imagePool.get(22 + page));
                                                                                                    if(length>=24) {
                                                                                                        imageFrame23.setImage((Image) imagePool.get(23 + page));

        }}}}}}}}}}}}}}}}}}}}}}}}}

    public void importTemplate(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println(selectedFile);

        SImage templateImage = new SImage(selectedFile);


    }
    }

