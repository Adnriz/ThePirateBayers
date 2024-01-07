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

    public MovieManager() throws SQLException, IOException {
        movieDAO = new MovieDAO();
    }

    public List<Movie> getAllMoviesWithCategories() throws SQLException {
        return movieDAO.getAllMoviesWithCategories();
    }


}
