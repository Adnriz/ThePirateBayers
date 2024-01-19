package DAL.DAO;

import BE.Category;
import BE.Movie;
import DAL.DBConnector;
import DAL.Interfaces.IMovieDAO;
import Util.MovieException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO implements IMovieDAO {
    private DBConnector databaseConnector = DBConnector.getInstance();

    public MovieDAO() throws MovieException {

    }

    /**
     * Gets all movies and categories associated with the movieID in the CatMovie Table.
     *
     * @return allMovies
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public List<Movie> getAllMoviesWithCategories() throws MovieException {
        List<Movie> allMovies = new ArrayList<>();
        String SQL = "SELECT Movie.id AS MovieId, Movie.Title AS MovieTitle, Movie.PersonalRating, Movie.IMDBRating, Movie.Filepath, Movie.Lastview, Category.id AS CategoryId, Category.Category " +
                "FROM Movie " +
                "LEFT JOIN CatMovie ON Movie.id = CatMovie.MovieId " +
                "LEFT JOIN Category ON CatMovie.CategoryId = Category.id";
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            Movie currentMovie = null;

            while (rs.next()) {
                int movieId = rs.getInt("MovieId");
                if (currentMovie == null || currentMovie.getId() != movieId) {
                    // Creates new Movie object to store information
                    currentMovie = extractMovie(rs);
                    allMovies.add(currentMovie);
                } else {
                    // If it's a new Movie object, means it need to populate the Categories of the new movie.
                    int categoryId = rs.getInt("CategoryId");
                    String category = rs.getString("Category");
                    currentMovie.addCategory(new Category(categoryId, category));
                }
            }
        } catch (SQLException e) {
            throw new MovieException("Could not retrieve movies with categories from the database", e);
        }
        return allMovies;
    }

    //////////////////////////////////
    ////       Helper Methods     ////
    /// getAllMoviesWithCategories ///
    //////////////////////////////////

    /**
     * Method created a movie object with all the data from the DB. and then returns the Movie object.
     *
     * @param rs
     * @return the Movie object made from extracted details.
     * @throws MovieException If there is a problem with the database access.
     */
    private Movie extractMovie(ResultSet rs) throws MovieException {

        try {
            int movieId = rs.getInt("MovieId");
            String title = rs.getString("MovieTitle");
            double personalRating = rs.getDouble("PersonalRating");
            double imdbRating = rs.getDouble("IMDBRating");
            String filePath = rs.getString("Filepath");
            String lastView = rs.getString("Lastview");

            int categoryId = rs.getInt("CategoryId");
            String category = rs.getString("Category");

            Movie movie = new Movie(title, personalRating, imdbRating, filePath, lastView, movieId);

            movie.addCategory(new Category(categoryId, category));

            return movie;
        } catch (SQLException ex) {
            throw new MovieException("Could not get movie data from database", ex);
        }
    }

    /**
     * Fetches categories for a specific movie based on its ID.
     *
     * @param movieId The ID of the movie.
     * @return A list of categories associated with the movie.
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public List<Category> getCategoriesForMovie(int movieId) throws MovieException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT Category.id, Category.Category FROM CatMovie " +
                "JOIN Category ON CatMovie.CategoryId = Category.id " +
                "WHERE CatMovie.MovieId = ?;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int categoryId = rs.getInt("id");
                    String categoryName = rs.getString("Category");
                    categories.add(new Category(categoryId, categoryName));
                }
            }
        } catch (SQLException ex) {
            throw new MovieException("Could not get categories in movies from database.", ex);
        }
        return categories;
    }

    /**
     * Inserts a new movie into the database and links it with its categories.
     *
     * @param movie The Movie object to insert.
     * @return The Movie object with its generated ID.
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public Movie createMovie(Movie movie) throws MovieException {
        String sql = "INSERT INTO Movie (Title, PersonalRating, IMDBRating, FilePath) VALUES (?, ?, ?, ?);";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setMovieStatementParameters(pstmt, movie);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movie.setId(generatedKeys.getInt(1));
                }
            }
            linkMovieWithCategories(movie);
            return movie;
        } catch (SQLException ex) {
            throw new MovieException("Could not create movie in database", ex);
        }
    }

    ////////////////////////
    //// Helper Methods ////
    ////  createMovie   ////
    ////////////////////////

    private void setMovieStatementParameters(PreparedStatement pstmt, Movie movie) throws SQLException{
        pstmt.setString(1, movie.getMovieTitle());
        pstmt.setDouble(2, movie.getPersonalRating());
        pstmt.setDouble(3, movie.getImdbRating());
        pstmt.setString(4, movie.getFilePath());
    }

    public void linkMovieWithCategories(Movie movie) throws MovieException {
        String sql = "INSERT INTO CatMovie (CategoryId, MovieId) VALUES (?, ?);";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Integer categoryId : movie.getCategoryIds()) {
                pstmt.setInt(1, categoryId);
                pstmt.setInt(2, movie.getId());
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new MovieException("Could not link movies with categories", ex);
        }
    }

    /**
     * Deletes a movie from the database.
     * Deletes any references to the movie in the CatMovie table, then deletes the movie from the Movie table.
     *
     * @param movie The movie to be deleted.
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public void deleteMovie(Movie movie) throws MovieException {

        String deleteCatMovieSQL = "DELETE FROM CatMovie WHERE Movieid = ?";
        String deleteMovieSQL = "DELETE FROM Movie WHERE ID = ?";

        try (Connection conn = databaseConnector.getConnection()) {
            // delete referenced entries from CatMovie table
            executeUpdate(conn, deleteCatMovieSQL, movie.getId());
            // delete movie from Movie table
            executeUpdate(conn, deleteMovieSQL, movie.getId());
        } catch (SQLException ex) {
            throw new MovieException("Could not delete movie from the database", ex);
        }
    }

    ////////////////////////
    //// Helper Methods ////
    ////  deleteMovie   ////
    ////////////////////////

    private void executeUpdate(Connection conn, String sql, int id) throws SQLException{
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

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
    @Override
    public void updateMovie(int id, String title, double newPersonalRating, double newImdbRating, String filePath) throws MovieException {
        // SQL query to update the Movie table
        String updateQuery = "UPDATE Movie SET Title = ?, PersonalRating = ?, IMDBRating = ?, Filepath = ? WHERE id = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Set the values for the placeholders in the query
            preparedStatement.setString(1, title);
            preparedStatement.setDouble(2, newPersonalRating);
            preparedStatement.setDouble(3, newImdbRating);
            preparedStatement.setString(4, filePath);
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new MovieException("Could not update movie in database", ex);
        }
    }

    /**
     * Updates the last viewed date of a movie in the database.
     *
     * @param movie The movie to update last view date.
     * @param formattedDate The new last viewed date, in a formatted string.
     * @throws MovieException if there is an error executing the SQL command.
     */
    @Override
    public void updateLastView(Movie movie, String formattedDate) throws MovieException {
        String sql = "UPDATE Movie SET Lastview = ? WHERE id = ?";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {//method to get connection

            // set the corresponding parameters
            pstmt.setString(1, formattedDate);
            pstmt.setInt(2, movie.getId());

            // update the Movies table
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new MovieException("Could not update last view in database", ex);
        }
    }

}