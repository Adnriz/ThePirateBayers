package GUI.Controller;

import BE.Category;
import BE.Movie;
import GUI.Model.CategoryModel;
import GUI.Model.MovieModel;
import Util.MovieException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewMovieController {

    @FXML
    private ComboBox<String> cbFileType;
    @FXML
    private Button btnSave;
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
    private TextField txtTitle;
    @FXML
    private TextField txtFilepath;

    private MovieModel movieModel;
    private CategoryModel categoryModel;

    ////////////////////////
    ////   Initialize   ////
    // NewMovieController //
    ////////////////////////

    public NewMovieController() throws MovieException {
        this.categoryModel = new CategoryModel();
        this.movieModel = new MovieModel();
    }

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
        setupSpinners();
    }

    ////////////////////////
    //// Helper Methods ////
    ////   Initialize   ////
    ////////////////////////

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
        movieCategories.remove("Empty");
        movieCategories.add(0, "Empty");

        configureComboBox(cbCategory1, movieCategories);
        configureComboBox(cbCategory2, movieCategories);
        configureComboBox(cbCategory3, movieCategories);
    }

    private void configureComboBox(ComboBox<String> comboBox, ObservableList<String> categoryOptions) {
        comboBox.getItems().clear();
        comboBox.setItems(categoryOptions);
        comboBox.getSelectionModel().select("Empty");
    }

    private void setupFileTypeBox(){
        ObservableList<String> fileTypes = FXCollections.observableArrayList(".mp4", ".mpeg4");

        cbFileType.getItems().clear();
        cbFileType.getItems().addAll(fileTypes);
        cbFileType.getSelectionModel().select(null);
    }

    private void setupSpinners() {
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

    ////////////////////////
    ////     Handle     ////
    ////    New Movie   ////
    ////////////////////////

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

    @FXML
    private void onClose() {
        closeWindow();
    }


    ////////////////////////
    //// Helper Methods ////
    ////   New Movie    ////
    ////////////////////////

    /**
     * Gathers user input from the input fields and creates a new Movie object.
     *
     * @return A Movie object populated with user input.
     */
    private Movie getUserInput() {
        String title = txtTitle.getText();
        if (title.isEmpty()){
            showAlert("Missing input", "Please enter a movie title and try again.");
            return null;}

        String filePath = construcFilePath();
        if (filePath == null) return null;

        List<Integer> categoryIds = getCategoryIds();

        return createMovieFromInput(title, spinnerIMDB.getValue(), spinnerPersonal.getValue(), filePath, categoryIds);
    }

    private String construcFilePath() {
        String fileName = txtFilepath.getText();
        String fileType = cbFileType.getValue();
        if (fileName.isEmpty() || fileType == null){
            showAlert("Missing input", "Please enter a filename, select a filetype and try again.");
            return null;}

        String filePath = "Movies/" + fileName + fileType;
        File movieFile = new File(filePath);
        if (!movieFile.exists()){
            displayError("Error", "The file: " + filePath + ", does not exist.");
            return null;
        }
        return filePath;
    }

    private List<Integer> getCategoryIds() {
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(convertCategoryNameToId(cbCategory1.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory2.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory3.getValue()));
        return categoryIds;
    }

    private int convertCategoryNameToId(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return -1; // Return -1 to indicate "not found"
        }
        return movieModel.getCategoryModel().getCategoryIDFromName(categoryName);
    }

    private Movie createMovieFromInput(String title, Double iMDBRating, Double personalRating, String filePath, List<Integer> categoryIds) {
        Movie newMovie = new Movie();
        newMovie.setMovieTitle(title);
        newMovie.setImdbRating(iMDBRating);
        newMovie.setPersonalRating(personalRating);
        newMovie.setFilePath(filePath);
        newMovie.setCategoryIds(categoryIds);
        return newMovie;
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    ////////////////////////
    //// Helper Methods ////
    ////    General     ////
    ////////////////////////

    private void displayError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
