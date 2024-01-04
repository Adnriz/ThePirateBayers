package BLL;

import BE.Movie;
import DAL.MovieDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO;

    public MovieManager() throws SQLException, IOException {
        movieDAO = new MovieDAO();
    }

    public List<Movie> getAllMovies() throws Exception {
        return movieDAO.getAllMovies();
    }
}
