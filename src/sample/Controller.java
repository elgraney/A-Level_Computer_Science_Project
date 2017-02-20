package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import javafx.scene.Scene;


//This class handles all possible actions from the main application interface
public class Controller extends BorderPane {
    //these variable are all necessary to reference because they are each individual objects in the interface.
    @FXML private MenuBar menuBar;
    @FXML private Button importButton;
    @FXML public Label boom;
    @FXML private Button importTemplate;

    //here are 24 seperate ImageView items, hence the 24 variables here. Each will display a different image.
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


    //these variables store vital, frequently used information about images that have been imported.
    private ImageView[] imageFrameList;
    private ArrayList<SImage> SImagePool = new ArrayList<SImage>();
    private ArrayList<Image> imagePool = new ArrayList<Image>();
    private SImage templateSImage;
    private BufferedImage templateImage;


    private Integer page = 0;

    //This initialises the interface with a new set of image displays, starting at page 0
    public void innit(Stage primaryStage) {
        imageFrameList = new ImageView[]{imageFrame0, imageFrame1, imageFrame2, imageFrame3, imageFrame4, imageFrame5, imageFrame6, imageFrame7, imageFrame8, imageFrame9, imageFrame10, imageFrame11, imageFrame12, imageFrame13, imageFrame14, imageFrame15, imageFrame16, imageFrame17, imageFrame18, imageFrame19, imageFrame20, imageFrame21, imageFrame22, imageFrame23};
        pageLabel.setText(Integer.toString(page + 1));
    }

    //This is activated when the "Generate" button is pressed
    //it takes inputs from a new popup window,then passes them over to imageFactory to begin the main process.
    public void beginGenerationPhase() {

        //this code sets up a new window with a new instance of this controller.
        Pane popupPane = new Pane();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneratePopUp.fxml"));
        GeneratePopupController controller = new GeneratePopupController();
        loader.setController(controller);
        try {
            popupPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(popupPane);
        stage.setScene(scene);
        stage.setTitle("Output Preferences");
        stage.initModality(Modality.APPLICATION_MODAL);
        controller.innit(stage);
        stage.showAndWait();

        System.out.println("Output res index: " + controller.outputResolutionInt);
        System.out.println("Gen style index: " + controller.generationStyleInt);
        System.out.println("out format index: " + controller.outputFormatInt);

        int outputResolution = 3000;
        int generationStyle = controller.generationStyleInt + 1;
        String outputFormat = "jpg";


        switch (controller.outputResolutionInt) {
            case 0:
                outputResolution = 1500;
                break;
            case 1:
                outputResolution = 3000;
                break;
            case 2:
                outputResolution = 6000;
                break;
            case 3:
                outputResolution = 9000;
                break;
        }
        switch (controller.outputFormatInt) {
            case 0:
                outputFormat = "jpg";
                break;
            case 1:
                outputFormat = "png";
                break;
            case 2:
                outputFormat = "gif";
                break;
        }

        System.out.println(outputResolution + ", " + outputFormat + ", " + generationStyle);
        //make editable.
        int analysisLevel = 3;

        //the main process begins
        File outputFile = ImageFactory.generate(templateSImage, SImagePool, templateImage, analysisLevel, outputResolution, generationStyle, outputFormat);

        //now an output image has been returned
        //a new window is setup to display this output.
        BorderPane outputPane = new BorderPane();
        Stage outputStage = new Stage();
        FXMLLoader outputLoader = new FXMLLoader(getClass().getResource("OutputWindow.fxml"));
        OutputWindowController outputController = new OutputWindowController();
        outputLoader.setController(outputController);

        try {
            outputPane = outputLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene outputScene = new Scene(outputPane);
        outputStage.setScene(outputScene);
        outputStage.setTitle("Output View");
        outputStage.show();
        outputController.innit(outputFile, outputFormat, stage);
        outputController.showOutputImage();
    }


    //This is activated when Select New Images is pressed.
    //it allows the user to select images from their file directory and import them into the program
    public void importImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image files");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        System.out.println(selectedFiles);
        //this loop takes every selected file, creates a new "SImage" from it, then displays it in the interfaces
        for (File selectedFile : selectedFiles) {
            SImage currentImage = new SImage(selectedFile, 3);
            try {
                SImagePool.add(currentImage);
                //SImagePool is the collection of all imported images in SImage form.
                //ImagePool is the collection of all imported images in FXImage form. This is needed to display them.
                if (currentImage.getWidth() > 500 || currentImage.getHeight() > 500) {
                    imagePool.add(new Image(new FileInputStream(currentImage.file), currentImage.getWidth() / 5, currentImage.getHeight() / 5, false, false));
                } else if (currentImage.getWidth() > 1000 || currentImage.getHeight() > 1000) {
                    imagePool.add(new Image(new FileInputStream(currentImage.file), currentImage.getWidth() / 10, currentImage.getHeight() / 10, false, false));
                } else {
                    imagePool.add(new Image(new FileInputStream(currentImage.file)));
                }
                //This if statement resizes the displayed version of the image so that the program doesn't use too much memory
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            updateImagePool(imagePool);
        }
    }


    //This method updates the display whenever anything related to the interface changes.
    private void updateImagePool(List imagePool){
        clearPool();
        pageLabel.setText(Integer.toString(page+1));
        int length =  imagePool.size();
        int startIndex = (page * 24);
        int endIndex;

        //the page number is used to calculate which images from the array need to be displayed.

        if (length>(page + 1)*24-1){
            endIndex = (page+1)*24 -1;
        }
        else{
            endIndex = (page * 24) + (length % 24) - 1;
        }
        for (int index = startIndex; index <= endIndex; index++){
            imageFrameList[(index % 24)].setImage( (Image) imagePool.get(index));
        }
        }


//this method is similar to importImage() except ony one image can be imported at a time and it is stored seperately
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

    //increments the page if it possible to based on the number of imported images. (wont display an empty page)
    public void nextPage(){
        if (((page+1)*24)<= imagePool.size()){
            page = page +1;
            updateImagePool(imagePool);
        }
        else{
            System.out.print("no next page");
        }
    }

    //decrements the page if the page is greater than 0.
    public void previousPage(){
        if(page > 0) {
            page = page - 1;

            updateImagePool(imagePool);
        }
        else{
            System.out.println("No previous page");
        }

    }
    //removes the current contents of every frame so that new images can be displayed.
    private void clearPool(){
        for (ImageView frame: imageFrameList){
            frame.setImage(null);
        }
    }
}

