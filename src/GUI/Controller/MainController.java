package GUI.Controller;

import GUI.Model.MovieModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import BE.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.time.LocalDate;


public class MainController {

    @FXML
    private Button btnApplyFilters;
    @FXML
    private Button btnClearFilters;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnNewMovie;
    @FXML
    private Button btnUpdate;
    @FXML
    private ComboBox<String> cbCategory1;
    @FXML
    private ComboBox<String> cbCategory2;
    @FXML
    private ComboBox<String> cbCategory3;
    @FXML
    private Spinner<Double> spinnerIMDB;
    @FXML
    private Spinner<Double> spinnerPersonal;
    @FXML
    private TableView<Movie> tblviewMovies;
    @FXML
    private TableColumn<Movie,String> colTitle;
    @FXML
    private TableColumn<Movie, String> colCategory;
    @FXML
    private TableColumn<Movie, ?> colLastViewed;
    @FXML
    private TableColumn<Movie,Double> colPersonal;
    @FXML
    private TableColumn<Movie,Double> calIMDB;
    @FXML
    private TextField txtSearch;

    public void initialize(){
        setupInteractable();
    }


    @FXML
    private void onNewMovie(ActionEvent actionEvent) throws IOException
    {
        Stage stage = loadStage("/NewMovieWindow.fxml", "New Movie");
        stage.show();
    }

    /**
     * Collects all methods that handles the input controls.
     */
    private void setupInteractable()
    {
        setupCategoryBoxes();
        spinnersENGAGE();
    }

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
    /**
     * Loads a new stage (window) with the specified FXML file and title.
     *
     * @param fxmlPath The path to the FXML file.
     * @param title The title of the new stage.
     * @return Stage The newly created stage.
     * @throws IOException If there is an error loading the FXML file.
     */
    private Stage loadStage(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        return stage;
    }


}
