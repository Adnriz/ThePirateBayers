package GUI.Controller;
import GUI.Model.CategoryModel;
import GUI.Model.MovieModel;

import BE.Movie;
import BE.Category;

import Util.MovieException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import javax.xml.transform.Result;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MainController {


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
    private ListView<Category> listCategories;
    @FXML
    private TableColumn<Movie, String> colTitle;
    @FXML
    private TableColumn<Movie, String> colCategories;
    @FXML
    private TableColumn<Movie, String> colLastView;
    @FXML
    private TableColumn<Movie, Double> colPersonal;
    @FXML
    private TableColumn<Movie, Double> colIMDB;
    @FXML
    private TextField txtSearch;
    @FXML
    private TextField txtAddCategory;
    @FXML
    private Label errorLbl;
    private List<Movie> outdatedMoviesList;
    @FXML
    private ListView outdatedMovieView;
    private MovieModel movieModel;
    private CategoryModel categoryModel;


    public MainController() {
        try {
            movieModel = new MovieModel();
            categoryModel = new CategoryModel();
            outdatedMoviesList = new ArrayList<>();
        }
        catch (MovieException ex){
            displayError(ex);
        }
    }

    /**
     * Initializes the controller.
     */
    public void initialize() throws MovieException {
        setupInteractable();
        movieModel.checkForOldMovies();
    }

    private static void wait(int ms) {
    }
    /**
     * Handles the action to open the NewMovie window.
     *
     * @param actionEvent The event that triggered the action.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @FXML
    private void onNewMovie(ActionEvent actionEvent) throws MovieException {

       try {
           // Load NewMovieWindow
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewMovieWindow.fxml"));
           Parent root = loader.load();

           //Get the controller
           //NewMovieController newMovieController = loader.getController();

           // Show the window and wait for it to close
           Stage stage = new Stage();
           stage.setTitle("New Movie");
           stage.setScene(new Scene(root));
           stage.showAndWait();

           // Refresh the TableView when NewMovieWindow is closed
           //tblviewMovies.refresh();
           movieModel.refreshMovies();
       } catch (IOException ex) {
           displayError(ex);
       }
    }

    @FXML
    private void btnUpdateAction(ActionEvent actionEvent) throws IOException, MovieException {
        if (tblviewMovies.getSelectionModel().getSelectedItem() != null) {
            Movie selectedMovie = tblviewMovies.getSelectionModel().getSelectedItem();

            // Loading new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateMovieWindow.fxml"));
            Parent root = loader.load();

            // Get the controller of the new stage
            UpdateMovieController updateMovieController = loader.getController();

            // Pass the selected movie and reference to MainController to UpdateMovieController
            updateMovieController.setMovie(selectedMovie);

            // Show the window
            Stage stage = new Stage();
            stage.setTitle("Update Movie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            movieModel.refreshMovies();
        } else {
            showAlert("Error", "Please select a movie to update");
        }
    }

    /**
     * Collects all methods that handles the input controls.
     */
    private void setupInteractable() {
        setupCategoryBoxes();
        spinnersENGAGE();
        setupMovieTableview();
        setupListViewCategories();


        // Adds a listener to the search field, so that it updates it realtime.
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> onFilterSearch());
    }


    private void setupMovieTableview() {
        tblviewMovies.setItems(movieModel.getObservableMovies());
        colTitle.setCellValueFactory(new PropertyValueFactory<>("MovieTitle"));
        colPersonal.setCellValueFactory(new PropertyValueFactory<>("PersonalRating"));
        colIMDB.setCellValueFactory(new PropertyValueFactory<>("ImdbRating"));
        colLastView.setCellValueFactory(new PropertyValueFactory<>("lastView"));

        /**
         * Cell value factory for the colCategories TableColumn. It retrieves the list of categories
         * from the Movie object and sets the cell value to a formatted, sorted, and filtered string
         * of category names. If the list of categories is null, it sets an empty string as the cell value.
         */
        colCategories.setCellValueFactory(cellData -> {
            List<Category> categories = cellData.getValue().getCategories();
            if (categories != null) {
                return new SimpleStringProperty(movieModel.getCategoriesAsStringSorted(cellData.getValue()));
            } else {
                return new SimpleStringProperty("");
            }
        });
    }


    /**
     * Method to set up the Spinners on launch.
     */
    private void spinnersENGAGE() {
        // Sets the parameters for the values, from 0.0 to 10.0, and the increment to 0.1
        SpinnerValueFactory<Double> valueFactoryIMDB = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        // Is a builtin class from javaFX, that formats the numbers to they display 7.0 instead of 7
        valueFactoryIMDB.setConverter(new DoubleStringConverter());

        SpinnerValueFactory<Double> valueFactoryPersonal = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        valueFactoryPersonal.setConverter(new DoubleStringConverter());
        // Sets the parameters for the spinners with the code from above.
        spinnerIMDB.setValueFactory(valueFactoryIMDB);
        spinnerPersonal.setValueFactory(valueFactoryPersonal);
    }

    /**
     * Method to set up all the combo boxes with categories on launch
     */
    private void setupCategoryBoxes() {
        // Fetch categories from the CategoryModel
        ObservableList<Category> categories = categoryModel.getCategories();

        // Create a list of category names
        ObservableList<String> categoryNames = categories.stream()
                // Only get the names and not the ids
                .map(Category::getName)
                // consolidate into a list to parse into the comboboxes
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Sets the Combo Boxes up, so that "Empty" always is the first option.
        cbCategory1.getItems().clear();
        cbCategory1.getItems().addAll(categoryNames);
        cbCategory1.getItems().remove("Empty");
        cbCategory1.getItems().add(0, "Empty");
        cbCategory1.getSelectionModel().select("Empty");

        cbCategory2.getItems().clear();
        cbCategory2.getItems().addAll(categoryNames);
        cbCategory2.getItems().remove("Empty");
        cbCategory2.getItems().add(0, "Empty");
        cbCategory2.getSelectionModel().select("Empty");

        cbCategory3.getItems().clear();
        cbCategory3.getItems().addAll(categoryNames);
        cbCategory3.getItems().remove("Empty");
        cbCategory3.getItems().add(0, "Empty");
        cbCategory3.getSelectionModel().select("Empty");

    }

    public void deleteMovie(ActionEvent event){
        // Retrieve the selected movie from tblviewMovies
        Movie selectedMovie = tblviewMovies.getSelectionModel().getSelectedItem();
        // Ensure a movie was selected
        if (selectedMovie != null)
        {
            boolean confirmDelete = showConfirmationAlert("Delete movie", "Are you sure would want to delete:" + selectedMovie.getMovieTitle() + "?");
            if (confirmDelete)
            {
                try {
                    // Delete the selected movie from the database
                    movieModel.deleteMovie(selectedMovie);
                    // Update the TableView by removing the selected movie
                    tblviewMovies.getItems().remove(selectedMovie);
                } catch (MovieException ex){
                    displayError(ex);
                }
            }
        }
    }

    private void onFilterSearch() {
        String searchText = txtSearch.getText();
        if (!searchText.isEmpty()) {
            try {
                List<Movie> searchResult = movieModel.searchMovies(searchText);
                updateTableView(searchResult);
            } catch (MovieException ex) {
                displayError(ex);
            }
        } else {
            // If the search field is empty, show all movies
            try {
                movieModel.refreshMovies();
            } catch (MovieException ex) {
                displayError(ex);
            }
        }
    }

    private void displayError(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("An error occurred: " + ex.getMessage());
        alert.setContentText("Please try again or contact support if the problem persists.");
        alert.showAndWait();
    }

    private void updateTableView(List<Movie> movies) {
        tblviewMovies.getItems().clear();
        tblviewMovies.getItems().addAll(movies);
    }

    /**
     * Loads a new stage (window) with the specified FXML file and title.
     *
     * @param fxmlPath The path to the FXML file.
     * @param title    The title of the new stage.
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


    private void setupListViewCategories() {
        listCategories.setItems(categoryModel.getCategories());
    }
    @FXML
    private void addCategory()
    {
        String categoryName = txtAddCategory.getText();
        Category newCategory = new Category(-1, categoryName);

        try {
            boolean confirmCreation = showConfirmationAlert("Add Category",
                    "Would you like to add the category " + txtAddCategory.getText() + "?");
            if (confirmCreation) {
                categoryModel.addCategory(newCategory);

                listCategories.setItems(categoryModel.getCategories());
                txtAddCategory.clear();

                setupCategoryBoxes();
            }
        } catch (MovieException ex) {
            displayError(ex);
        }
    }


    @FXML
    private void deleteCategory(ActionEvent actionEvent) {
        Category selectedCategory = listCategories.getSelectionModel().getSelectedItem();

        if (selectedCategory != null) {
            try {
                boolean confirmDelete = showConfirmationAlert("Delete Category",
                        "Are you sure you want to delete this category " + selectedCategory.getName() + "?");
                if (confirmDelete)
                {
                    categoryModel.deleteCategory(selectedCategory);
                    listCategories.setItems(categoryModel.getCategories());
                    txtAddCategory.clear();
                    setupCategoryBoxes();
                }
            } catch (Exception e)
            {
                showAlert("ERROR WHILE DELETING",
                        "Something went wrong while trying to delete this " +
                                selectedCategory.getName() + " category");
            }
        } else {
            showAlert("Selection Required","Please select a category to delete");
        }
    }

    /**
     * This method is used to play the movie.
     * It gets the selected movie from your TableView.
     * If a movie has been selected (i.e., the selected movie is not null),
     * If the user's desktop can open files (which is usually the case),
     * On this new thread, it tries to open the file with the default system application.
     * If an IOException is thrown (for example, if the file does not exist),
     * the catch block prints an error message.
     */

    // How to handle exceptions here? CHATGPT suggests to split them up, so that onPlayMovie Handles the higher exception
    // and the second method just throws the SQL and IO Exception as a MovieException
    @FXML
    private void onPlayMovie(){
        Movie selectedMovie = tblviewMovies.getSelectionModel().getSelectedItem();

        if (selectedMovie != null) {
            File movieFile = new File(selectedMovie.getFilePath());
            if (Desktop.isDesktopSupported()) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(movieFile);
                        updateLastViewDate(selectedMovie);
                    } catch (IOException ex) {
                        System.out.println("An error occurred while trying to play the movie: " + ex.getMessage());
                    } catch (MovieException ex) {
                        throw new RuntimeException(ex);
                    }
                }).start();
            }
        }
    }

    /**
    * Handles the apply filter action event. Collects filter criteria from the UI, applies them, and updates the UI with the filtered movies.
     *
     * @param actionEvent The action event that triggers this method.
     */
    @FXML
    private void onApplyFilters(ActionEvent actionEvent) {

            List<String> selectedCategories = getSelectedCategories();
            double minIMDBRating = spinnerIMDB.getValue();
            double minPersonalRating = spinnerPersonal.getValue();
        try {
            movieModel.filterMovies(minIMDBRating, minPersonalRating, selectedCategories);
            tblviewMovies.setItems(movieModel.getMoviesToBeViewed());
        } catch (MovieException ex) {
            displayError(ex);
        }
    }

    /**
     * Collects the selected categories from the ComboBoxes.
     *
     * @return A list of selected category strings.
     */
    private List<String> getSelectedCategories() {
        List<String> selectedCategories = new ArrayList<>();

        if (cbCategory1.getValue() != null && !"Empty".equals(cbCategory1.getValue())) {
            selectedCategories.add(cbCategory1.getValue());
        }
        if (cbCategory2.getValue() != null && !"Empty".equals(cbCategory2.getValue())) {
            selectedCategories.add(cbCategory2.getValue());
        }
        if (cbCategory3.getValue() != null && !"Empty".equals(cbCategory3.getValue())) {
            selectedCategories.add(cbCategory3.getValue());
        }

        return selectedCategories;
    }

    /**
     * Handles the clear filters action event. Resets the filter UI components and shows all
     * Resets all filter controls to their default values and displays all movies in the TableView.
     */
    @FXML
    private void onClearFilters(ActionEvent actionEvent) {
        cbCategory1.getSelectionModel().select("Empty");
        cbCategory2.getSelectionModel().select("Empty");
        cbCategory3.getSelectionModel().select("Empty");

        //Resetting the spinners
        spinnerIMDB.getValueFactory().setValue(0.0);
        spinnerPersonal.getValueFactory().setValue(0.0);

        //Resetting the tableview to show all movies
        tblviewMovies.setItems(movieModel.getObservableMovies());
    }
    

    /**
     * Displays a confirmation alert with the specified title and content.
     *
     * @param title   The title of the alert dialog.
     * @param content The content message displayed in the alert dialog.
     * @return boolean Returns true if the user clicks 'Yes', and false if the user clicks 'No' or closes the dialog.
     */
    private boolean showConfirmationAlert(String title, String content) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle(title);
        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void updateLastViewDate(Movie movie) throws MovieException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(new Date(System.currentTimeMillis()));
        movie.setLastView(formattedDate);
        movieModel.updateLastView(movie, formattedDate);
        movieModel.refreshMovies();
    }



}
