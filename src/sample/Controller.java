package sample;

import com.sun.javafx.iio.ImageFrame;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.Scene;


//This class handles all possible actions from the main application interface
public class Controller extends BorderPane {
    //these variable are all necessary to reference because they are each individual objects in the interface.
    @FXML private MenuBar menuBar;
    @FXML private Button importButton;
    @FXML public Label boom;
    @FXML private Button importTemplate;

    //here are 24 separate ImageView items, hence the 24 variables here. Each will display a different image.
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
    //Again there are 24 separate Vboxs here, each containing their own imageview, hence the 24 variables here.
    @FXML private VBox vbox0;
    @FXML private VBox vbox1;
    @FXML private VBox vbox2;
    @FXML private VBox vbox3;
    @FXML private VBox vbox4;
    @FXML private VBox vbox5;

    @FXML private VBox vbox6;
    @FXML private VBox vbox7;
    @FXML private VBox vbox8;
    @FXML private VBox vbox9;
    @FXML private VBox vbox10;
    @FXML private VBox vbox11;

    @FXML private VBox vbox12;
    @FXML private VBox vbox13;
    @FXML private VBox vbox14;
    @FXML private VBox vbox15;
    @FXML private VBox vbox16;
    @FXML private VBox vbox17;

    @FXML private VBox vbox18;
    @FXML private VBox vbox19;
    @FXML private VBox vbox20;
    @FXML private VBox vbox21;
    @FXML private VBox vbox22;
    @FXML private VBox vbox23;

    @FXML private ImageView templateFrame = null;

    //These variables are for labels that will be changed to represent data as the program is running
    @FXML private  Label pageLabel;
    @FXML private Label noImagesLabel;
    @FXML private Label noSelectedImagesLabel;
    @FXML private Label noDeselectedImagesLabel;
    @FXML private Label MCRLabel;


    //these variables store vital, frequently used information about images that have been imported.
    private ImageView[] imageFrameList;
    private VBox[] vboxList;
    private ArrayList<SImage> SImagePool = new ArrayList<SImage>();
    private ArrayList<Image> imagePool = new ArrayList<Image>();
    private SImage templateSImage;
    private BufferedImage templateImage;
    private Stage stage;

    private int noImages;
    private int noSelectedImages;
    private int noDeselectedImages;
    private int widthRatio;
    private int heightRatio;

    private Integer page = 0;


    //This initialises the interface with a new set of image displays, starting at page 0
    public void innit(Stage primaryStage) {
        this.stage = primaryStage;
        imageFrameList = new ImageView[]{imageFrame0, imageFrame1, imageFrame2, imageFrame3, imageFrame4, imageFrame5, imageFrame6, imageFrame7, imageFrame8, imageFrame9, imageFrame10, imageFrame11, imageFrame12, imageFrame13, imageFrame14, imageFrame15, imageFrame16, imageFrame17, imageFrame18, imageFrame19, imageFrame20, imageFrame21, imageFrame22, imageFrame23};
        vboxList = new VBox[]{vbox0, vbox1, vbox2, vbox3, vbox4, vbox5, vbox6, vbox7, vbox8, vbox9, vbox10, vbox11, vbox12, vbox13, vbox14, vbox15, vbox16, vbox17, vbox18, vbox19, vbox20, vbox21, vbox22, vbox23};
        pageLabel.setText(Integer.toString(page + 1));
        noImagesLabel.setText("0");
        noSelectedImagesLabel.setText("0");;
        noDeselectedImagesLabel.setText("0");;
        MCRLabel.setText("N/A");;
    }

    //This is activated when the "Generate" button is pressed
    //it takes inputs from a new popup window,then passes them over to imageFactory to begin the main process.
    //there are many checks here that prevent the continuation of the generation phase if the imported images are insufficient.
    public void checkGenData(){
        if (templateImage != null){
            if (imagePool.size()>10){
                int selectedImageCount=0;
                for (SImage image : SImagePool){
                    if (image.selected == true){
                        selectedImageCount += 1;
                    }
                }
                if (selectedImageCount>=10){
                    if (ImageFactory.generating != true){
                        try {
                            beginGenerationPhase();
                        } catch (ImageFactory.GenerationException e) {
                            //The following message is displayed if there is any issue within the generation (takes place in ImageFactory)
                            JOptionPane.showMessageDialog(null, "Generation Failure.", "Generation Warning", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null,"You have not selected enough images to generate a decent image, please select more.", "Generation Warning", JOptionPane.ERROR_MESSAGE);
                }
            }
            else{
                JOptionPane.showMessageDialog(null,"You have not imported enough images to generate a decent image, please add more.", "Generation Warning", JOptionPane.ERROR_MESSAGE);
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "You have not selected a template image, please select one.", "Generation Warning", JOptionPane.ERROR_MESSAGE);

        }
    }
    private void beginGenerationPhase() throws ImageFactory.GenerationException {

        //this code sets up a new window with a new instance of this controller.
        //the window displays generation options
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
        stage.getIcons().add(new Image("file:icon2.png"));
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
            case 3:
                outputFormat = "bmp";
        }

        System.out.println(outputResolution + ", " + outputFormat + ", " + generationStyle);
        //make editable.
        int analysisLevel = 3;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Beginning generation");
        alert.setHeaderText("Beginning generation");
        alert.setContentText("This will likely take a considerable amount of time. More than enough to make a cup of tea.");
        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        alert.show();

        //the main process begins
        File outputFile = ImageFactory.generate(templateSImage, SImagePool, templateImage, analysisLevel, outputResolution, generationStyle, outputFormat);

        alert.close();

        //The following fetches the data from the ImageFactory class that will be displayed in the output window
        int noSections = ImageFactory.getNoSections();
        int noUniqueImages = ImageFactory.getNoUniqueImages();


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
        outputController.innit(outputFile, outputFormat, stage, noSections, noUniqueImages);
        outputController.showOutputImage();
    }


    //This is activated when Select New Images is pressed.
    //it allows the user to select images from their file directory and import them into the program
    public void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image files");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        System.out.println(selectedFiles);

        int importSize =0;
        if (selectedFiles != null) {
            importSize = selectedFiles.size();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Importing your images, please wait.");
        alert.setHeaderText("Importing...");
        alert.setContentText("This will likely take around "+(Math.round(importSize*0.25))+" seconds");
        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        alert.show();

        importAnalysis(selectedFiles);
        alert.close();
    }

    public void importAnalysis(List<File> selectedFiles) {

        if (selectedFiles != null) {
            int count = 0;
            for (File selectedFile : selectedFiles) {
                count += 1;
                SImage currentImage = new SImage(selectedFile);
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
            }
        }
        updateImagePool(imagePool);
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
        int selectedImages=0;
        for (SImage image : SImagePool){
            if (image.selected == true){
                selectedImages+=1;
            }
        }
        for (int index = startIndex; index <= endIndex; index++){
            imageFrameList[(index % 24)].setImage( (Image) imagePool.get(index));
            if (SImagePool.get(index).selected == false){
                vboxList[index % 24].setStyle("-fx-border-color: #ff9e9b; -fx-border-width: 3px; -fx-background-color: #ff9e9b");
            }
            else{
                vboxList[index % 24].setStyle("-fx-border-color: #a5d9ff; -fx-border-width: 3px;-fx-background-color: #a5d9ff");
            }
        }
        poolData(selectedImages);
    }

    private void poolData(int selectedImages){
        int noImages = SImagePool.size();
        int deselectedImages = noImages - selectedImages;
        int widthDisplayRatio=0;
        int heightDisplayRatio=0;
        double mostCommonRatio = ImageFactory.getMostCommonRatio(SImagePool);
        for (SImage image : SImagePool){
            if(mostCommonRatio*image.getHeight() == image.getWidth()){
                int greatestCommonDivisor =gcd(image.getWidth(), image.getHeight());
                widthDisplayRatio = image.getWidth()/greatestCommonDivisor;
                heightDisplayRatio = image.getHeight()/greatestCommonDivisor;
                break;
            }
        }
        this.noImages = noImages;
        this.noSelectedImages = selectedImages;
        this.noDeselectedImages = deselectedImages;
        this.widthRatio = widthDisplayRatio;
        this.heightRatio = heightDisplayRatio;
        noImagesLabel.setText(Integer.toString(noImages));
        noSelectedImagesLabel.setText(Integer.toString(noSelectedImages));
        noDeselectedImagesLabel.setText(Integer.toString(noDeselectedImages));
        MCRLabel.setText(Integer.toString(widthRatio)+":"+Integer.toString(heightRatio));

    }
    //Euclid's algorithm for finding the highest common divisor.
    public static int gcd(int p, int q) {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }

    //this method is similar to importImage() except ony one image can be imported at a time and it is stored separately
    public void importTemplate() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif","*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {

            System.out.println(selectedFile);


            templateSImage = new SImage(selectedFile);

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
        for (VBox vbox: vboxList){
            vbox.setStyle(null);
        }
    }

    private void handleClick(int image){
        if(imagePool.size()>image+page*24){
            System.out.println("found image "+image);
            int SImageIndex = image + page*24;
            System.out.println(SImageIndex);
            if (SImagePool.get(SImageIndex).selected == true){
                SImagePool.get(SImageIndex).selected = false;
            }
            else{
                SImagePool.get(SImageIndex).selected = true;
            }
        }
        updateImagePool(imagePool);
    }

    public void image0Click(){
        handleClick(0);
    }
    public void image1Click(){
        handleClick(1);
    }
    public void image2Click(){
        handleClick(2);
    }
    public void image3Click(){
        handleClick(3);
    }
    public void image4Click(){
        handleClick(4);
    }
    public void image5Click(){
        handleClick(5);
    }
    public void image6Click(){
        handleClick(6);
    }
    public void image7Click(){
        handleClick(7);
    }
    public void image8Click(){
        handleClick(8);
    }
    public void image9Click(){
        handleClick(9);
    }
    public void image10Click(){
        handleClick(10);
    }
    public void image11Click(){
        handleClick(11);
    }
    public void image12Click(){
        handleClick(12);
    }
    public void image13Click(){
        handleClick(13);
    }
    public void image14Click(){
        handleClick(14);
    }
    public void image15Click(){
        handleClick(15);
    }
    public void image16Click(){
        handleClick(16);
    }
    public void image17Click(){
        handleClick(17);
    }
    public void image18Click(){
        handleClick(18);
    }
    public void image19Click(){
        handleClick(19);
    }
    public void image20Click(){
        handleClick(20);
    }
    public void image21Click(){
        handleClick(21);
    }
    public void image22Click(){
        handleClick(22);
    }
    public void image23Click(){
        handleClick(23);
    }

    public void newPool(){
        clearPool();
        SImagePool = new ArrayList<SImage>();
        imagePool =  new ArrayList<Image>();
        updateImagePool(imagePool);
    }
    public void close(){
        stage.close();
    }
    public void about(){
        Stage webStage = new Stage();
        webStage.setTitle("About");

        WebView  browser = new WebView();
        WebEngine engine = browser.getEngine();
        String url = Controller.class.getResource("ABOUT.htm").toExternalForm();
        engine.load(url);

        StackPane sp = new StackPane();
        sp.getChildren().add(browser);

        Scene root = new Scene(sp);
        webStage.getIcons().add(new Image("file:aboutIcon.jpg"));
        webStage.setScene(root);
        webStage.show();
    }
    public void help(){
        Stage webStage = new Stage();
        webStage.setTitle("help");

        WebView  browser = new WebView();
        WebEngine engine = browser.getEngine();
        String url = Controller.class.getResource("HELP.htm").toExternalForm();
        engine.load(url);

        StackPane sp = new StackPane();
        sp.getChildren().add(browser);

        Scene root = new Scene(sp);
        webStage.getIcons().add(new Image("file:helpIcon.jpg"));

        webStage.setScene(root);
        webStage.show();

    }

    public void save() throws IOException{
        System.out.println("SAVE");
        File fOut = new File("saveFile.txt");
        FileOutputStream fos = new FileOutputStream(fOut);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));
        for (SImage image : SImagePool) {
                bufferedWriter.write((image.file.getAbsolutePath()));
                bufferedWriter.newLine();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saving");
        alert.setHeaderText("Saving");
        alert.setContentText("Saving complete");
        alert.show();
        bufferedWriter.close();

    }
    public void load() throws  IOException{
        newPool();
        System.out.println("Load");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Loading");
        alert.setHeaderText("Loading");
        alert.setContentText("loading complete");
        alert.show();
        List<File> selectedFiles = new ArrayList<File>();

        FileReader fr = new FileReader("saveFile.txt");
        BufferedReader bufferedReader = new BufferedReader(fr);
        while (true){
            String file = bufferedReader.readLine();
            Boolean existing = true;
            if(file!=null){
                try{
                    new FileInputStream(new File(file));
                    existing = true;
                }
                catch(IOException exception){
                    existing=false;
                }
                if(existing == true) {
                    selectedFiles.add(new File(file));
                }

            }
            else{
                break;
            }
        }
        importAnalysis(selectedFiles);
    }

}




