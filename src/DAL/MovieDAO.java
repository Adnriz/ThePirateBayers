package DAL;

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

    public List<Movie> getAllMovies() throws SQLException{
        List<Movie> allMovies = new ArrayList<>();
        String SQL = "SELECT * FROM Movie;";
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                allMovies.add(extractMovieFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Could not retrieve songs from database", e);
        }
        return allMovies;
    }

    ////////////////////////
    //// Helper Methods ////
    ////////////////////////

    /**
     * Extracts song information from ResultSet into a movie object.
     *
     * @param rs ResultSet containing movie data.
     * @return A movie object.
     * @throws SQLException for errors in accessing the ResultSet.
     */
    private Movie extractMovieFromResultSet(ResultSet rs) throws SQLException {
        String title = rs.getString("title");
        Double personalRating = rs.getDouble("PersonalRating");
        Double imdbRating = rs.getDouble("IMDBRating");
        String filepath = rs.getString("Filepath");
        String lastviewed = rs.getString("Lastview");
        int id = rs.getInt("id");

        return new Movie(title,personalRating,imdbRating,filepath,lastviewed,id);
    }
    /**
     * Prepares a PreparedStatement with the movie data.
     * @param stmt The PreparedStatement to be prepared.
     * @param movie The movie providing the data.
     * @throws SQLException for statement preparation errors.
     */
    private void prepareMovieStatement(PreparedStatement stmt, Movie movie) throws SQLException {
        stmt.setString(2, movie.getMovieTitle());
        stmt.setDouble(3,movie.getPersonalRating());
        stmt.setDouble(4,movie.getImdbRating());
        stmt.setString(5, movie.getFilePath());
        stmt.setString(6, movie.getLastView());
    }

}
