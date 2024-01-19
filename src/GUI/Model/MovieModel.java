package GUI.Model;

import BE.Category;
import BE.Movie;
import BLL.Managers.MovieManager;
import Util.MovieException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MovieModel {
    private ObservableList<Movie> availableMovies;
    private MovieManager movieManager;
    private CategoryModel categoryModel;

    private ObservableList<Movie> moviesToBeViewed;

    private List<Movie> outdatedMoviesAndBadRatingList;

    private List<Movie> filteredMovies = null;


    public MovieModel() throws MovieException {
        movieManager = new MovieManager();
        categoryModel = new CategoryModel();
        availableMovies = FXCollections.observableArrayList(movieManager.getAllMoviesWithCategories());
        moviesToBeViewed = FXCollections.observableArrayList();

    }

    public ObservableList<Movie> getObservableMovies() {
        return availableMovies;
    }

    /**
     * This method ensures that the categories are sorted
     * by taking in a movie object, and using a stream method to convert the list.
     * and uses filter to remove the category Empty, so it isnt displayed to the user.
     * It then gets sorted in the map.
     * then collect gathers the stream back into a List<String>
     * Lastly it joins the string in the list, into a singular string, and separates them with ", "
     * With this it returns the input list as a string, filtered, sorted and presentable to the user.
     * @param movie
     * @return sorted String
     */
    public String getCategoriesAsStringSorted(Movie movie) {
        if (movie.getCategories() == null || movie.getCategories().isEmpty()) {
            return "";
        }

        List<String> sortedCategories = movie.getCategories().stream()
                .filter(category -> category != null && category.getName() != null && !category.getName().equalsIgnoreCase("Empty"))
                .map(Category::getName)
                .sorted()
                .collect(Collectors.toList());

        return String.join(", ", sortedCategories);
    }

    /**
     * Returns the CategoryModel used by MovieModel.
     *
     * @return The CategoryModel instance.
     */
    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    /**
     * Returns the list of available movies as an ObservableList.
     *
     * @return ObservableList of movies.
     */
    public ObservableList<Movie> getAvailableMovies() {
        return availableMovies;
    }

    /**
     * Adds a new movie to the model. This includes creating the movie in the database,
     * fetching its categories, and updating the list of available movies.
     *
     * @param movie The movie to be added.
     * @throws SQLException If a database access error occurs.
     */
    public void addMovie(Movie movie) throws MovieException {
        Movie createdMovie = movieManager.createMovie(movie);

        List<Category> categories = movieManager.getCategoriesForMovie(createdMovie.getId());
        createdMovie.setCategories(categories);

        availableMovies.add(createdMovie);
    }

    public List<Movie> searchMovies(String query) throws MovieException
    {
        List<Movie> searchList;
        if (filteredMovies != null){
            searchList = filteredMovies;
        } else {
            searchList = movieManager.getAllMoviesWithCategories();
        } return movieManager.searchMovies(searchList, query);
    }

    public void refreshMovies() throws MovieException {
            List<Movie> allMovies = movieManager.getAllMoviesWithCategories();
            availableMovies.setAll(allMovies);
    }
    public void updateMovie(int id, String title, double newPersonalRating, double newImdbRating, String filePath) throws MovieException {
        movieManager.updateMovieInfo(id, title, newPersonalRating, newImdbRating, filePath);
    }
    public void linkCatMov(Movie movie) throws MovieException {
        movieManager.linkCatMov(movie);
    }

    public void deleteMovie(Movie selectedMovie) throws MovieException {
        movieManager.deleteMovie(selectedMovie);
    }

    public void updateLastView(Movie movie, String formattedDate) throws MovieException {
        movieManager.updateLastView(movie, formattedDate);
    }

    public boolean inputCheck(String userInput){

        if (userInput == null || userInput.trim().isEmpty()){
            return false;
        }

        if (userInput.length() > 255){
            return false;
        }
        return true;
    }

    /**
     * Applies filtering to movies based on given criteria and updates the observable list of movies.
     *
     * @param minIMDBRating The minimum IMDb rating for the filter.
     * @param minPersonalRating The minimum personal rating for the filter.
     * @param selectedCategories The categories to include in the filter.
     * @throws SQLException If there is a problem accessing the movie data.
     */
    public List<Movie> filterMovies(double minIMDBRating, double minPersonalRating, List<String> selectedCategories) throws MovieException {
        filteredMovies = movieManager.filterMovies(minIMDBRating, minPersonalRating, selectedCategories);
        return filteredMovies;
    }

    public List<Movie> resetFilters() throws MovieException {
        filteredMovies = null;
        return movieManager.getAllMoviesWithCategories();
    }

    public List<Movie> checkForOldOrBadRatedMovies() throws MovieException {
        List<Movie> allMovies = getAvailableMovies();
        return allMovies.stream()
                .filter(this::isMovieOutdatedOrBadRated)
                .collect(Collectors.toList());
        }

    ///////////////////////////
    ////  Helper Methods   ////
    //// CheckForOldMovies ////
    ///////////////////////////

    private boolean isMovieOutdatedOrBadRated(Movie movie) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String lastViewedDateString = movie.getLastView();
            if (lastViewedDateString != null && !lastViewedDateString.isEmpty()){
                Date lastViewedDate = dateFormat.parse(lastViewedDateString);
                long timeDifference = System.currentTimeMillis() - lastViewedDate.getTime();
                long twoYearsInMillis = 2 * 365 * 24 * 60 * 60 * 1000L;
                return timeDifference >= twoYearsInMillis || movie.getPersonalRating() < 6.0;
            }
        } catch (ParseException e) {
            displayError("Error parsing date for movie: " + movie.getMovieTitle(), e.getMessage());
        }
        return false;
    }

    private void displayError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}



