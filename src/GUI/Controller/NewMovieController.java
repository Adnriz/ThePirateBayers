package GUI.Controller;

import BE.Category;
import BE.Movie;
import GUI.Model.CategoryModel;
import GUI.Model.MovieModel;
import Util.MovieException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewMovieController {

    @FXML
    private ComboBox<String> cbFileType;
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
    private Movie currentMovie;
    private MovieModel movieModel;
    private CategoryModel categoryModel;


    public NewMovieController() throws MovieException {
        this.categoryModel = new CategoryModel();
        this.movieModel = new MovieModel();
    }

    /**
     * Initializes the controller.
     */
    public void initialize(){
        setupInteractable();
    }

    /**
     * Collects all methods that handles the input controls.
     */
    private void setupInteractable()
    {
        setupCategoryBoxes();
        setupFileTypeBox();
        spinnersENGAGE();
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
        // Fetch categories from the CategoryModel
        ObservableList<Category> categories = categoryModel.getCategories();

        // Create a list of category names
        ObservableList<String> movieCategories = categories.stream()
                // Only get the names and not the ids
                .map(Category::getName)
                // consolidate into a list to parse into the comboboxes
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Sets the Combo Boxes up, so that "Empty" always is the first option.
        cbCategory1.getItems().clear();
        cbCategory1.getItems().addAll(movieCategories);
        cbCategory1.getItems().remove("Empty");
        cbCategory1.getItems().add(0, "Empty");
        cbCategory1.getSelectionModel().select("Empty");

        cbCategory2.getItems().clear();
        cbCategory2.getItems().addAll(movieCategories);
        cbCategory2.getItems().remove("Empty");
        cbCategory2.getItems().add(0, "Empty");
        cbCategory2.getSelectionModel().select("Empty");

        cbCategory3.getItems().clear();
        cbCategory3.getItems().addAll(movieCategories);
        cbCategory3.getItems().remove("Empty");
        cbCategory3.getItems().add(0, "Empty");
        cbCategory3.getSelectionModel().select("Empty");
    }

    private void setupFileTypeBox(){
        ObservableList<String> fileTypes = FXCollections.observableArrayList(".mp4", ".mpeg4");

        cbFileType.getItems().clear();
        cbFileType.getItems().addAll(fileTypes);
        cbFileType.getSelectionModel().select(null);
    }


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
        String fileName = txtFilepath.getText();
        String fileType = cbFileType.getValue();

        if (fileName.isEmpty() || fileType == null){
            displayError("Error", "Please enter a filename and select a file type.");
            return null;
        }

        String filepath = "Movies/" + fileName + fileType;
        File movieFile = new File(filepath);
        if (!movieFile.exists()){
            displayError("Error", "The file: " + filepath + ", does not exist.");
            return null;
        }

        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(convertCategoryNameToId(cbCategory1.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory2.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory3.getValue()));

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
            return -1; // Return -1 to indicate "not found"
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
            if (movie != null){
                movieModel.addMovie(movie);
                closeWindow();
            }
        } catch (MovieException e) {
            displayError("Database error", "Error saving movie to database.");
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
    @FXML
    private void onClose(ActionEvent actionEvent) {
        closeWindow();
    }


    ////////////////////////
    //// Helper Methods ////
    ////    General     ////
    ////////////////////////

    /**
     * Shows an alert dialog displaying an error.
     */
    private void displayError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
