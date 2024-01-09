package BLL;

import BE.Category;
import BE.Movie;
import DAL.MovieDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO;
    private final MovieSearcher movieSearcher;

    public MovieManager() throws SQLException, IOException {
        movieDAO = new MovieDAO();
        movieSearcher = new MovieSearcher();
    }

    /**
     * Retrieves all movies and their associated categories from the database.
     *
     * @return A list of all movies with their categories.
     * @throws SQLException If there is a problem accessing the database.
     */
    public List<Movie> getAllMoviesWithCategories() throws SQLException {
        return movieDAO.getAllMoviesWithCategories();
    }

    /**
     * Retrieves categories for a specific movie.
     *
     * @param movieId The ID of the movie.
     * @return A list of categories associated with the movie.
     * @throws SQLException If there is a problem accessing the database.
     */
    public List<Category> getCategoriesForMovie(int movieId) throws SQLException {
        return movieDAO.getCategoriesForMovie(movieId);
    }

    /**
     * Creates a new movie in the database.
     *
     * @param movie The Movie object to be created in the database.
     * @return The created Movie object.
     * @throws SQLException If there is a problem accessing the database.
     */
    public Movie createMovie(Movie movie) throws SQLException {
        return movieDAO.createMovie(movie);
    }

    public List<Movie> searchMovies(String query) throws Exception {
        List<Movie> allMovies = getAllMoviesWithCategories();
        List<Movie> titleSearchResult = movieSearcher.search(allMovies, query);
        List<Movie> categorySearchResult = movieSearcher.searchByCategory(allMovies, query);

        // Merge the search results (if needed).
        // For simplicity, the example below appends the category search results to the title search results.
        titleSearchResult.addAll(categorySearchResult);

        return titleSearchResult;


    }
}
