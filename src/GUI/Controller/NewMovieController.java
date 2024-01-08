package GUI.Controller;

import BE.Movie;
import GUI.Model.MovieModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    private MovieModel movieModel;


    public NewMovieController() throws SQLException, IOException {
    }

    /**
     * Initializes the controller.
     */
    public void initialize(){
        setupInteractable();
    }

    /**
     * Sets the MovieModel for this controller.
     *
     * @param movieModel The MovieModel to be used.
     */
    public void setMovieModel(MovieModel movieModel) {
        this.movieModel = movieModel;
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
        ObservableList<String> movieCategories = FXCollections.observableArrayList("Empty","Fantasy","Action","Western","Adventure","Musical","Comedy","Romance","Horror",
                "Mystery","Animation","Documentary","Drama","Thriller","Science Fiction","Crime","History","Sports","Family","Film-Noir","Short","War","Game-Show","Reality");

        cbCategory1.getItems().clear();
        cbCategory1.getItems().addAll(movieCategories);
        cbCategory1.getSelectionModel().select("Empty");

        cbCategory2.getItems().clear();
        cbCategory2.getItems().addAll(movieCategories);
        cbCategory2.getSelectionModel().select("Empty");

        cbCategory3.getItems().clear();
        cbCategory3.getItems().addAll(movieCategories);
        cbCategory3.getSelectionModel().select("Empty");

    }
    // CODE SMELL DONE MAYBE PROBABLY //
    /**
     * Gathers user input from the form fields and creates a new Movie object.
     *
     * @return A Movie object populated with user input.
     */
    @FXML
    private Movie getUserInput() {
        String title = txtTitle.getText();
        double imdbRating = spinnerIMDB.getValue();
        double personalRating = spinnerPersonal.getValue();
        String filepath = txtFilepath.getText();

        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(convertCategoryNameToId(cbCategory1.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory2.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory3.getValue()));

        // Remove any invalid category IDs (-1 for not found)
        categoryIds.removeIf(id -> id == -1 || id == null);

        // Creating the Movie object with the collected information
        Movie newMovie = new Movie();
        newMovie.setMovieTitle(title);
        newMovie.setImdbRating(imdbRating);
        newMovie.setPersonalRating(personalRating);
        newMovie.setFilePath(filepath);
        newMovie.setCategoryIds(categoryIds);

        return newMovie;
    }

    /**
     * Converts a category name to its ID.
     *
     * @param categoryName The name of the category.
     * @return The ID of the category, or -1 if not found.
     */
    private int convertCategoryNameToId(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return -1; // Return -1 or any other default value to indicate "not found"
        }
        return movieModel.getCategoryModel().getCategoryIDFromName(categoryName);
    }

    /**
     * Handles the save action when the Save button is clicked.
     * It gathers user input, adds the movie to the model, and closes the window.
     */
    @FXML
    private void onSave() {
        try {
            Movie movie = getUserInput();
            movieModel.addMovie(movie);
            closeWindow();
        } catch (SQLException e) {
            // Handle SQL exceptions, maybe show an error message
        }
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the save action when the Close button is clicked.
     * Closes the current window.
     */
    public void onClose(ActionEvent actionEvent) {
        closeWindow();
    }


}
