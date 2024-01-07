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
        String sql = "DELETE FROM Movie WHERE id = ?";
        try (Connection conn = databaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, movie.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not delete movie", ex);
        }
    }
}
