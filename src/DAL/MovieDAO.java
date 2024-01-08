package DAL;

import BE.Category;
import BE.Movie;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    private DBConnector databaseConnector;

    public MovieDAO() throws SQLException, IOException {
        databaseConnector = new DBConnector();
    }

    /**
     * Gets all movies and categories associated with the movieID in the CatMovie Table.
     * Which then gets sent up in the layers.
     * @return allMovies
     * @throws SQLException
     */
    public List<Movie> getAllMoviesWithCategories() throws SQLException {
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
                    // If its a new Movie object, means it need to populate the Categories of the new movie.
                    int categoryId = rs.getInt("CategoryId");
                    String category = rs.getString("Category");
                    currentMovie.addCategory(new Category(categoryId, category));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Could not retrieve movies with categories from the database", e);
        }
        return allMovies;
    }

    /**
     * Fetches categories for a specific movie based on its ID.
     *
     * @param movieId The ID of the movie.
     * @return A list of categories associated with the movie.
     * @throws SQLException If a database access error occurs.
     */
    public List<Category> getCategoriesForMovie(int movieId) throws SQLException {
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
        }
        return categories;
    }

    /**
     * Inserts a new movie into the database and links it with its categories.
     *
     * @param movie The Movie object to insert.
     * @return The Movie object with its generated ID.
     * @throws SQLException If a database access error occurs.
     */
    public Movie createMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO Movie (Title, PersonalRating, IMDBRating, FilePath) VALUES (?, ?, ?, ?);";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, movie.getMovieTitle());
            pstmt.setDouble(2, movie.getPersonalRating());
            pstmt.setDouble(3, movie.getImdbRating());
            pstmt.setString(4, movie.getFilePath());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movie.setId(generatedKeys.getInt(1));
                }
            }

            linkMovieWithCategories(movie);
            return movie;
        }
    }

    private void linkMovieWithCategories(Movie movie) throws SQLException {
        String sql = "INSERT INTO CatMovie (CategoryId, MovieId) VALUES (?, ?);";
        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Integer categoryId : movie.getCategoryIds()) {
                pstmt.setInt(1, categoryId);
                pstmt.setInt(2, movie.getId());
                pstmt.executeUpdate();
            }
        }
    }

    ////////////////////////
    //// Helper Methods ////
    ////////////////////////

    /**
     * Method created a movie object with all the data from the DB. and then returns the Movie object.
     * @param rs
     * @return Movie
     * @throws SQLException
     */
    private Movie extractMovie(ResultSet rs) throws SQLException {
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
    }

    public void deleteMovie(Movie movie) throws Exception {
        if (databaseConnector == null) {
            try {
                databaseConnector = new DBConnector();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String deleteCatMovieSQL = "DELETE FROM CatMovie WHERE Movieid = ?";
        String deleteMovieSQL = "DELETE FROM Movie WHERE ID = ?";

        try (Connection conn = databaseConnector.getConnection()) {

            // delete referenced entries from CatMovie table
            PreparedStatement stmt1 = conn.prepareStatement(deleteCatMovieSQL);
            stmt1.setInt(1, movie.getId());
            stmt1.executeUpdate();

            // delete movie from Movie table
            PreparedStatement stmt2 = conn.prepareStatement(deleteMovieSQL);
            stmt2.setInt(1, movie.getId());
            stmt2.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not delete movie", ex);
        }
    }
}
