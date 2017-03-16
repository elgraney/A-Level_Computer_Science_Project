
package sample;

        import com.sun.javafx.stage.StageHelper;
        import javafx.beans.value.ChangeListener;
        import javafx.beans.value.ObservableValue;
        import javafx.collections.FXCollections;
        import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.ChoiceBox;
        import javafx.scene.image.ImageView;
        import javafx.stage.Stage;

/**
 * Created by Matthew on 19/02/2017.
 */



public class GeneratePopupController {
    //the variables with tag @FXML are all necessary to reference because they are each individual objects in the interface that are being changed in the program in some way.
    @FXML private ChoiceBox outputResolutionBox;
    @FXML private ChoiceBox outputFormatBox;
    @FXML private ChoiceBox generationStyleBox;
    @FXML private Button generationButton;

    public boolean beginGenerationPressed = false;
    public int outputResolutionInt = 1;
    public int generationStyleInt;
    public int outputFormatInt;
    private Stage stage;

    //innit sets up the contents of the pop-up window
    public void innit(Stage primaryStage) {

        stage = primaryStage;

        //the default value of the Choice box is set to standard and the other options are listed
        outputResolutionBox.setValue("Standard");
        outputResolutionBox.setItems(FXCollections.observableArrayList("Low", "Standard", "High", "Ultra"));

        outputResolutionBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            //This returns the int index of the selected choice eg low = 0, standard =1 etc
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                outputResolutionInt = newValue.intValue();
            }
        });
        //the default value of the Choice box is set to JPG and the other options are listed
        outputFormatBox.setValue("JPG");
        outputFormatBox.setItems(FXCollections.observableArrayList("JPG", "PNG", "GIF", "BMP"));
        outputFormatBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                outputFormatInt = newValue.intValue();
            }
        });
        //the default value of the Choice box is set to Ordered and the other option is listed
        generationStyleBox.setValue("Ordered");
        generationStyleBox.setItems(FXCollections.observableArrayList("Ordered", "Messy"));
        generationStyleBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                generationStyleInt = newValue.intValue();
            }
        });
    }
    //when the generation begins, this window closes
    public void beginGeneration(){
        beginGenerationPressed = true;
        stage.close();
    }
}