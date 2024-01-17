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

public class UpdateMovieController {

    @FXML
    private ComboBox cbFileType;
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
    private CategoryModel categoryModel;


    ///////////////////////////
    ////    Initialize     ////
    // UpdateMovieController //
    ///////////////////////////

    public UpdateMovieController() throws Exception {
        movieModel = new MovieModel();
        categoryModel = new CategoryModel();
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        //Calling method to update the ui
        updateUIWithMovieData();
    }

    /////////////////////////
    ////      Handle     ////
    ////   Update Movie  ////
    /////////////////////////

    @FXML
    private void onSaveAction() throws MovieException {
        boolean updateSuccessful = movieDetailsUpdate(movie);
        movieCategoryUpdate(movie);

        if (updateSuccessful) {
            Stage stage = (Stage) txtTitle.getScene().getWindow();
            stage.close();
        }
    }
    //Method that closes the UI
    @FXML
    private void onClose() {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }

    ////////////////////////
    //// Helper Methods ////
    ////   Initialize   ////
    ////////////////////////

    private void updateUIWithMovieData() {
        //Checks if a movie object is parsed
        if (movie != null) {
            //Sets the data from the movie to the fields in the update UI
            txtTitle.setText(movie.getMovieTitle());
            spinnersENGAGE(movie);
            setupCategoryBoxes(movie);
            setupFileTypeBox();

            String filePath = movie.getFilePath();
            String filenameWithType = filePath.substring(filePath.lastIndexOf("/") + 1);
            int filetypeIndex = filenameWithType.lastIndexOf(".");

            if (filetypeIndex != -1) {
                String filename = filenameWithType.substring(0, filetypeIndex);
                String filetype = filenameWithType.substring(filetypeIndex);

                txtFilepath.setText(filename);
                cbFileType.getSelectionModel().select(filetype);
            }
        }
    }

    private void spinnersENGAGE(Movie movie) {
        //Sets the parameters for the values, from 0.0 to 10.0, and the increment to 0.1 & with the movies current ratings as default
        SpinnerValueFactory<Double> valueFactoryIMDB = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, movie.getImdbRating(), 0.1);
        //Is a builtin class from javaFX, that formats the numbers to they display 7.0 instead of 7
        valueFactoryIMDB.setConverter(new DoubleStringConverter());

        SpinnerValueFactory<Double> valueFactoryPersonal = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, movie.getPersonalRating(), 0.1);
        valueFactoryPersonal.setConverter(new DoubleStringConverter());
        //Sets the parameters for the spinners with the code from above.
        spinnerIMDB.setValueFactory(valueFactoryIMDB);
        spinnerPersonal.setValueFactory(valueFactoryPersonal);
    }

    private void setupCategoryBoxes(Movie movie) {
        // Fetch categories from the CategoryModel
        ObservableList<Category> categories = categoryModel.getCategories();

        // Create a list of category names
        ObservableList<String> movieCategories = categories.stream()
                // Only get the names and not the ids
                .map(Category::getName)
                // consolidate into a list to parse into the comboboxes
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Set up ComboBox 1
        cbCategory1.getItems().clear();
        cbCategory1.getItems().addAll(movieCategories);
        if (movie.getCategories().size() > 0) {
            cbCategory1.getItems().remove("Empty");
            cbCategory1.getItems().add(0, "Empty");
            cbCategory1.getSelectionModel().select(movie.getCategories().get(0).getName());
        }

        // Set up ComboBox 2
        cbCategory2.getItems().clear();
        cbCategory2.getItems().addAll(movieCategories);
        if (movie.getCategories().size() > 1) {
            cbCategory2.getItems().remove("Empty");
            cbCategory2.getItems().add(0, "Empty");
            cbCategory2.getSelectionModel().select(movie.getCategories().get(1).getName());
        }

        // Set up ComboBox 3
        cbCategory3.getItems().clear();
        cbCategory3.getItems().addAll(movieCategories);
        if (movie.getCategories().size() > 2) {
            cbCategory3.getItems().remove("Empty");
            cbCategory3.getItems().add(0, "Empty");
            cbCategory3.getSelectionModel().select(movie.getCategories().get(2).getName());
        }
    }

    private void setupFileTypeBox() {
        ObservableList<String> fileTypes = FXCollections.observableArrayList(".mp4", ".mpeg4");

        cbFileType.getItems().clear();
        cbFileType.getItems().addAll(fileTypes);
        cbFileType.getSelectionModel().select(null);
    }

    ////////////////////////
    //// Helper Methods ////
    ////  Update Movie  ////
    ////////////////////////

    private boolean movieDetailsUpdate(Movie movie) throws MovieException {
        if (!txtTitle.getText().isEmpty() && spinnerPersonal.getValue() != null && spinnerIMDB.getValue() != null) {
            //assigning the information to be updated
            int id = movie.getId();
            String title = txtTitle.getText();
            double newPersonalRating = (double) Math.round(spinnerPersonal.getValue() * 10) / 10;
            double newImdbRating = (double) Math.round(spinnerIMDB.getValue() * 10) / 10;

            String filename = txtFilepath.getText();
            String filetype = (String) cbFileType.getValue();
            String updatedFilePath = "Movies/" + filename + filetype;
            File updatedMovieFile = new File(updatedFilePath);

            if (!updatedMovieFile.exists()) {
                displayError("Error", "The file: " + updatedFilePath + ", does not exist.");
                return false; // Update failed
            } else {
                movieModel.updateMovie(id, title, newPersonalRating, newImdbRating, updatedFilePath);
                return true; // Update successful
            }
        }
        else {
            showAlert("Error", "Please fill out all the information");
            return false; // update failed
        }
    }

    private void movieCategoryUpdate(Movie movie) throws MovieException {
        int movieid = movie.getId();
        CategoryModel categoryModel = new CategoryModel();
        //deleting the old categories
        categoryModel.removeCategoriesFromMovie(movieid);

        //Adding the new Ids to the movie
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(convertCategoryNameToId(cbCategory1.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory2.getValue()));
        categoryIds.add(convertCategoryNameToId(cbCategory3.getValue()));
        try {
            movie.setCategoryIds(categoryIds);
            movieModel.linkCatMov(movie);
        } catch (MovieException ex){
            displayError("Update Category","Error updating categories for this movie");
        }
    }

    private int convertCategoryNameToId(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return -1; //Return -1 to tell that the category is not found
        }
        //Return the id of the category with set name
        return movieModel.getCategoryModel().getCategoryIDFromName(categoryName);
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
    /**
     * Shows an alert dialog displaying information.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}