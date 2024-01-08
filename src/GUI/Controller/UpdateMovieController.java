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

public class UpdateMovieController {
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
    private Movie movie;
    private MainController mainController;


    public UpdateMovieController() throws Exception {
        movieModel = new MovieModel();
    }


    // System.out.println(movie.getId() + " " + movie.getMovieTitle() + " " + movie.getPersonalRating() + " " + movie.getImdbRating());

   private void spinnersENGAGE(Movie movie) {
        // Sets the parameters for the values, from 0.0 to 10.0, and the increment to 0.1
        SpinnerValueFactory<Double> valueFactoryIMDB = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, movie.getImdbRating(), 0.1);
        // Is a builtin class from javaFX, that formats the numbers to they display 7.0 instead of 7
        valueFactoryIMDB.setConverter(new DoubleStringConverter());

        SpinnerValueFactory<Double> valueFactoryPersonal = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, movie.getPersonalRating(), 0.1);
        valueFactoryPersonal.setConverter(new DoubleStringConverter());
        // Sets the parameters for the spinners with the code from above.
        spinnerIMDB.setValueFactory(valueFactoryIMDB);
        spinnerPersonal.setValueFactory(valueFactoryPersonal);
    }


    private void setupCategoryBoxes(Movie movie) {
        // A list containing all the movie categories
        ObservableList<String> movieCategories = FXCollections.observableArrayList("Empty", "Fantasy", "Action", "Western", "Adventure", "Musical", "Comedy", "Romance", "Horror",
                "Mystery", "Animation", "Documentary", "Drama", "Thriller", "Science Fiction", "Crime", "History", "Sports", "Family", "Film-Noir", "Short", "War", "Game-Show", "Reality");

        // Set up ComboBox 1
        cbCategory1.getItems().clear();
        cbCategory1.getItems().addAll(movieCategories);
        if (movie.getCategories().size() > 0) {
            cbCategory1.getSelectionModel().select(movie.getCategories().get(0).getName());
        }

        // Set up ComboBox 2
        cbCategory2.getItems().clear();
        cbCategory2.getItems().addAll(movieCategories);
        if (movie.getCategories().size() > 1) {
            cbCategory2.getSelectionModel().select(movie.getCategories().get(1).getName());
        }

        // Set up ComboBox 3
        cbCategory3.getItems().clear();
        cbCategory3.getItems().addAll(movieCategories);
        if (movie.getCategories().size() > 2) {
            cbCategory3.getSelectionModel().select(movie.getCategories().get(2).getName());
        }
    }
    public void btnClose(ActionEvent actionEvent) {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }
    public void setMovie(Movie movie, MainController mainController) {
        this.movie = movie;
        this.mainController = mainController;

        // Call a method to update the UI components with movie data
        updateUIWithMovieData();
    }

    private void updateUIWithMovieData() {
        if (movie != null) {
            txtTitle.setText(movie.getMovieTitle());
            txtFilepath.setText(movie.getFilePath());
            spinnersENGAGE(movie);
            setupCategoryBoxes(movie);
        }
    }

}