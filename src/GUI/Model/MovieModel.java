package GUI.Model;

import BE.Category;
import BE.Movie;
import BLL.CategoryManager;
import BLL.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MovieModel {
    private ObservableList<Movie> availableMovies;
    private MovieManager movieManager;
    private CategoryModel categoryModel;


    public MovieModel() throws SQLException, IOException {
        movieManager = new MovieManager();
        categoryModel = new CategoryModel();
        availableMovies = FXCollections.observableArrayList(movieManager.getAllMoviesWithCategories());
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
                .filter(category -> !category.getName().equalsIgnoreCase("Empty"))
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
    public void addMovie(Movie movie) throws SQLException {
        Movie createdMovie = movieManager.createMovie(movie);

        List<Category> categories = movieManager.getCategoriesForMovie(createdMovie.getId());
        createdMovie.setCategories(categories);

        availableMovies.add(createdMovie);
    }
}



