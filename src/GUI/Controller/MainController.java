package GUI.Controller;
import DAL.MovieDAO;
import GUI.Model.MovieModel;

import BE.Movie;
import BE.Category;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


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
    private TableColumn<Movie,String> colTitle;
    @FXML
    private TableColumn<Movie, String> colCategories;
    @FXML
    private TableColumn<Movie, String> colLastView;
    @FXML
    private TableColumn<Movie,Double> colPersonal;
    @FXML
    private TableColumn<Movie,Double> colIMDB;
    @FXML
    private TextField txtSearch;
    private MovieModel movieModel;

    public MainController() throws Exception {
        movieModel = new MovieModel();
    }

    /**
     * Initializes the controller.
     */
    public void initialize(){
        setupInteractable();
    }

    /**
     * Handles the action to open the NewMovie window.
     *
     * @param actionEvent The event that triggered the action.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @FXML
    private void onNewMovie(ActionEvent actionEvent) throws IOException {
        // Load the NewMovieWindow
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewMovieWindow.fxml"));
        Parent root = loader.load();

        // Get the controller and pass the current MovieModel
        NewMovieController newMovieController = loader.getController();
        newMovieController.setMovieModel(movieModel);

        // Show the window and wait for it to close
        Stage stage = new Stage();
        stage.setTitle("New Movie");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        // Refresh the TableView
        tblviewMovies.refresh();
    }

    /**
     * Collects all methods that handles the input controls.
     */
    private void setupInteractable()
    {
        setupCategoryBoxes();
        spinnersENGAGE();
        setupMovieTableview();

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

    public void deleteMovie(ActionEvent event) throws SQLException, IOException {
        // Retrieve the selected movie from tblviewMovies
        Movie selectedMovie = tblviewMovies.getSelectionModel().getSelectedItem();

        // Ensure a movie was selected
        if (selectedMovie != null) {
            MovieDAO movieDao = new MovieDAO();

            try {
                // Delete the selected movie from the database
                movieDao.deleteMovie(selectedMovie);

                // Update the TableView by removing the selected movie
                tblviewMovies.getItems().remove(selectedMovie);
            } catch (Exception e) {
                // Handle the exception (e.g., show an error message)
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No movie selected!");
        }
    }

    private void onFilterSearch() {
        String searchText = txtSearch.getText();
        if (!searchText.isEmpty()) {
            try {
                List<Movie> searchResult = movieModel.searchMovies(searchText);
                updateTableView(searchResult);
            } catch (Exception e) {
                displayError(e);
            }
        } else {
            // If the search field is empty, show all movies
            try {
                movieModel.refreshMovies();
            } catch (Exception e) {
                displayError(e);
            }
        }
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("An error occurred: " + t.getMessage());
        alert.setContentText("Please try again or contact support if the problem persists.");
        alert.showAndWait();
    }

    private void updateTableView(List<Movie> movies){
        tblviewMovies.getItems().clear();
        tblviewMovies.getItems().addAll(movies);
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
