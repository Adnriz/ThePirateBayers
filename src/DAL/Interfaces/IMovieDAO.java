package DAL.Interfaces;

import BE.Category;
import BE.Movie;
import Util.MovieException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IMovieDAO {

    /**
     * Gets all movies and categories associated with the movieID in the CatMovie Table.
     *
     * @return allMovies
     * @throws MovieException if there is an error executing the SQL commands.
     */
    List<Movie> getAllMoviesWithCategories() throws MovieException;

    /**
     * Fetches categories for a specific movie based on its ID.
     *
     * @param movieId The ID of the movie.
     * @return A list of categories associated with the movie.
     * @throws MovieException if there is an error executing the SQL commands.
     */
    List<Category> getCategoriesForMovie(int movieId) throws MovieException;

    /**
     * Inserts a new movie into the database and links it with its categories.
     *
     * @param movie The Movie object to insert.
     * @return The Movie object with its generated ID.
     * @throws MovieException if there is an error executing the SQL commands.
     */
    Movie createMovie(Movie movie) throws MovieException;

    /**
     * Deletes a movie from the database.
     * Deletes any references to the movie in the CatMovie table, then deletes the movie from the Movie table.
     *
     * @param movie The movie to be deleted.
     * @throws MovieException if there is an error executing the SQL commands.
     */
    void deleteMovie(Movie movie) throws MovieException;

    /**
     * Updates the details of a movie in the database.
     *
     * @param id The ID of the movie.
     * @param title The new title.
     * @param newPersonalRating The new personal rating.
     * @param newImdbRating The new IMDb rating.
     * @param filePath The new file path.
     * @throws MovieException if there is an error executing the SQL command.
     */
    void updateMovie(int id, String title, double newPersonalRating, double newImdbRating, String filePath) throws MovieException;

    /**
     * Updates the last viewed date of a movie in the database.
     *
     * @param movie The movie to update last view date.
     * @param formattedDate The new last viewed date, in a formatted string.
     * @throws MovieException If there is a problem with the database access.
     */
    void updateLastView(Movie movie, String formattedDate) throws MovieException;

}
