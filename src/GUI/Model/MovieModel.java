package GUI.Model;

import BE.Category;
import BE.Movie;
import BLL.Managers.MovieManager;
import GUI.Controller.OutdatedController;
import Util.MovieException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MovieModel {
    private ObservableList<Movie> availableMovies;
    private MovieManager movieManager;
    private CategoryModel categoryModel;

    private ObservableList<Movie> moviesToBeViewed;

    private List<Movie> outdatedMoviesAndBadRatingList;


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
        return movieManager.searchMovies(query);
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
    private void setOutdatedMoviesAndBadRatingList(List<Movie> outdatedMoviesAndBadRatingList) {
        this.outdatedMoviesAndBadRatingList = outdatedMoviesAndBadRatingList;
    }
    public List<Movie> getOutdatedMoviesAndBadRatingList() {
        return outdatedMoviesAndBadRatingList;
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



    public void checkForOldMovies() throws MovieException {
        // First getting a list of all the movies
        ObservableList<Movie> allMovies = getAvailableMovies();
        // Setting the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        boolean oldMovie = false;

        List<Movie> outdatedMoviesAndBadRatingList = new ArrayList<>();
        // Loop that checks all movies, and adding the outdated ones to a list
        for (Movie movie : allMovies) {
            try {
                String lastViewedDateString = movie.getLastView();
                if (lastViewedDateString != null) {
                    java.util.Date lastViewedDate = dateFormat.parse(lastViewedDateString);
                    java.util.Date currentDate = new Date();
                    // Finding the difference between current date and last viewed date
                    long timeDifference = currentDate.getTime() - lastViewedDate.getTime();
                    long twoYearsInMillis = 2 * 365 * 24 * 60 * 60 * 1000L;
                    // Add the movie to the list if it hasn't been seen in two years or has a personal rating under 6.0
                    if (timeDifference >= twoYearsInMillis || movie.getPersonalRating() < 6.0) {
                        outdatedMoviesAndBadRatingList.add(movie);
                        oldMovie = true;
                    }
                }
            } catch (ParseException e) {
                // Handle the exception (print or log the error, etc.)
                System.out.println("An error occurred while processing a movie: " + e.getMessage());
                // Optionally, throw a MovieException with the original exception as its cause
                throw new MovieException("An error occurred while processing a movie.", e);
            }
        }

        if (oldMovie) {
            setOutdatedMoviesAndBadRatingList(outdatedMoviesAndBadRatingList);

            // Opening the window which shows the outdated movies
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OutdatedWindow.fxml"));
                Parent root = loader.load();

                OutdatedController outdatedController = loader.getController();
                outdatedController.setMovieModel(this);

                // Showing the stage
                Stage stage = new Stage();
                stage.setTitle("Update Movie");
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (IOException e) {
                // Handle the exception (print or log the error, etc.)
                System.out.println("An error occurred while opening the outdated window: " + e.getMessage());
                // Optionally, throw a MovieException with the original exception as its cause
                throw new MovieException("An error occurred while opening the outdated window.", e);
            }
        }
    }

    /**
     * Applies filtering to movies based on given criteria and updates the observable list of movies.
     *
     * @param minIMDBRating The minimum IMDb rating for the filter.
     * @param minPersonalRating The minimum personal rating for the filter.
     * @param selectedCategories The categories to include in the filter.
     * @throws SQLException If there is a problem accessing the movie data.
     */
    public void filterMovies(double minIMDBRating, double minPersonalRating, List<String> selectedCategories) throws MovieException {
        List<Movie> filteredMovies = movieManager.filterMovies(minIMDBRating, minPersonalRating, selectedCategories);
        moviesToBeViewed.clear();
        moviesToBeViewed.addAll(filteredMovies);
    }

    /**
     * Retrieves the observable list of movies to be viewed.
     *
     * @return An ObservableList of movies that have been filtered.
     */
    public ObservableList<Movie> getMoviesToBeViewed() {
        return moviesToBeViewed;
    }

}



