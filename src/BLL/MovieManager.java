package BLL;

import BE.Category;
import BE.Movie;
import DAL.MovieDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO;
    private final MovieSearcher movieSearcher;
    private final MovieFilter movieFilter;

    public MovieManager() throws SQLException, IOException {
        movieDAO = new MovieDAO();
        movieSearcher = new MovieSearcher();
        movieFilter = new MovieFilter();
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
    public void updateMovieInfo(int id, String title, double newPersonalRating, double newImdbRating, String filePath) {
        movieDAO.updateMovie(id, title, newPersonalRating, newImdbRating, filePath);
    }
    /**
     *  This method performs the search through the MovieSearcher.
     *  And combines the two results
     * @param query
     * @return
     * @throws Exception
     */
    public List<Movie> searchMovies(String query) throws Exception {
        // Retrieves a list with all movies and categories
        List<Movie> allMovies = getAllMoviesWithCategories();

        // Performs the search
        List<Movie> movieSearchResult = movieSearcher.search(allMovies, query);
        List<Movie> categorySearchResult = movieSearcher.searchByCategory(allMovies, query);

        // Consolidates the results
        movieSearchResult.addAll(categorySearchResult);

        return movieSearchResult;

    }
    public void linkCatMov(Movie movie) throws SQLException {
        movieDAO.linkMovieWithCategories(movie);
    }

    public void deleteMovie(Movie selectedMovie) throws SQLException {
        movieDAO.deleteMovie(selectedMovie);
    }

public void updateLastView(Movie movie, String formattedDate) throws SQLException {
        // Assuming movieDAO is your MovieDAO instance
        movieDAO.updateLastView(movie, formattedDate);
    }

    /**
     * Filters movies using specified criteria using the MovieFilter utility class.
     *
     * @param minIMDBRating The minimum IMDb rating for the filter.
     * @param minPersonalRating The minimum personal rating for the filter.
     * @param selectedCategories A list of categories for the filter.
     * @return A list of movies that meet the filtering criteria.
     * @throws SQLException If a database access error occurs.
     */
    public List<Movie> filterMovies(double minIMDBRating, double minPersonalRating, List<String> selectedCategories) throws SQLException {
        List<Movie> allMovies = getAllMoviesWithCategories();
        return movieFilter.filterMovies(allMovies, minIMDBRating, minPersonalRating, selectedCategories);
    }

}
