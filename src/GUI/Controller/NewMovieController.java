package GUI.Controller;

import BE.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.time.LocalDate;

public class NewMovieController {

    @FXML
    private Button btnClose;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cbCategory1;

    @FXML
    private ComboBox<String> cbCategory2;

    @FXML
    private ComboBox<String> cbCategory3;

    @FXML
    private DatePicker dateLastViewed;

    @FXML
    private Spinner<Double> spinnerIMDB;

    @FXML
    private Spinner<Double> spinnerPersonal;

    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtFilepath;

    public void initialize(){
        setupInteractable();
    }

    /**
     * Collects all methods that handles the input controls.
     */
    private void setupInteractable()
    {
        setupCategoryBoxes();
        spinnersENGAGE();
        // dateLastViewed.setValue(LocalDate.now());  // needs to be moved to when movie is updated, and or update when the movie is played on the database.
    }

    // :) CODE SMELL INCOMING, Maybe move it to another class, and give this one access to the method.
    /**
     * Method to set up the Spinners on launch.
     */
    private void spinnersENGAGE() {
        // Sets the parameters for the values, from 0.0 to 10.0, and the increment to 0.1
        SpinnerValueFactory<Double> valueFactoryIMDB = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 7.0, 0.1);
        // Is a builtin class from javaFX, that formats the numbers to they display 7.0 instead of 7
        valueFactoryIMDB.setConverter(new DoubleStringConverter());

        SpinnerValueFactory<Double> valueFactoryPersonal = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 7.0, 0.1);
        valueFactoryPersonal.setConverter(new DoubleStringConverter());
        // Sets the parameters for the spinners with the code from above.
        spinnerIMDB.setValueFactory(valueFactoryIMDB);
        spinnerPersonal.setValueFactory(valueFactoryPersonal);
    }

    /**
     * Method to set up all the combo boxes with categories on launch
     */
    private void setupCategoryBoxes()
    {
        // A list containing all the movie categories
        ObservableList<String> movieCategories = FXCollections.observableArrayList("Yep,This Isnt a Category :)","Fantasy","Action","Western","Adventure","Musical","Comedy","Romance","Horror",
                "Mystery","Animation","Documentary","Drama","Thriller","Science Fiction","Crime","History","Sports","Family","Film-Noir","Short","War","Game-Show","Reality");

        cbCategory1.getItems().clear();
        cbCategory1.getItems().addAll(movieCategories);
        cbCategory1.getSelectionModel().select("Yep This Isnt a Category :)");

        cbCategory2.getItems().clear();
        cbCategory2.getItems().addAll(movieCategories);
        cbCategory2.getSelectionModel().select("Yep This Isnt a Category :)");

        cbCategory3.getItems().clear();
        cbCategory3.getItems().addAll(movieCategories);
        cbCategory3.getSelectionModel().select("Yep This Isnt a Category :)");

    }

    @FXML
    private Movie getUserInput() {
        String title = txtTitle.getText();
        String category1 = cbCategory1.getValue();
        String category2 = cbCategory2.getValue();
        String category3 = cbCategory3.getValue();
        LocalDate lastViewed = dateLastViewed.getValue();
        double imdbRating = spinnerIMDB.getValue();
        double personalRating = spinnerPersonal.getValue();
        String filepath = txtFilepath.getText();

        return new Movie();
        // return new Movie(title,category1,category2,category3,imdbRating,personalRating,filepath); // needs constructor
    }
    public void btnClose(ActionEvent actionEvent) {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }

}
