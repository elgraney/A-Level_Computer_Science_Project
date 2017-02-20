
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

    @FXML
    private ChoiceBox outputResolutionBox;
    @FXML
    private ChoiceBox outputFormatBox;
    @FXML
    private ChoiceBox generationStyleBox;
    @FXML
    private Button generationButton;

    public int outputResolutionInt = 1;
    public int generationStyleInt;
    public int outputFormatInt;
    private Stage stage;


    public void innit(Stage primaryStage) {

        stage = primaryStage;


        outputResolutionBox.setValue("Standard");
        outputResolutionBox.setItems(FXCollections.observableArrayList("Low", "Standard", "High", "Ultra"));

        outputResolutionBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                outputResolutionInt = newValue.intValue();
            }
        });

        outputFormatBox.setValue("JPG");
        outputFormatBox.setItems(FXCollections.observableArrayList("JPG", "PNG", "GIF"));
        outputFormatBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                outputFormatInt = newValue.intValue();
            }
        });

        generationStyleBox.setValue("Ordered");
        generationStyleBox.setItems(FXCollections.observableArrayList("Ordered", "Messy"));
        generationStyleBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                generationStyleInt = newValue.intValue();
            }
        });
    }
    public void beginGeneration(){
        stage.close();
    }
}