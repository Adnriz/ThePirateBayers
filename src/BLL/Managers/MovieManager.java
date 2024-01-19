package BLL.Managers;

import BE.Category;
import BE.Movie;
import BLL.Util.MovieFilter;
import BLL.Util.MovieSearcher;
import DAL.DAO.MovieDAO;
import Util.MovieException;

import java.util.List;

public class MovieManager {

    private final MovieDAO movieDAO;
    private final MovieSearcher movieSearcher;
    private final MovieFilter movieFilter;

    public MovieManager() throws MovieException {
        movieDAO = new MovieDAO();
        movieSearcher = new MovieSearcher();
        movieFilter = new MovieFilter();
    }

    public List<Movie> getAllMoviesWithCategories() throws MovieException {
        return movieDAO.getAllMoviesWithCategories();
    }

    public List<Category> getCategoriesForMovie(int movieId) throws MovieException {
        return movieDAO.getCategoriesForMovie(movieId);
    }

    public Movie createMovie(Movie movie) throws MovieException {
        return movieDAO.createMovie(movie);
    }

    public void updateMovieInfo(int id, String title, double newPersonalRating, double newImdbRating, String filePath) throws MovieException {
        movieDAO.updateMovie(id, title, newPersonalRating, newImdbRating, filePath);
    }

    public List<Movie> searchMovies(List<Movie> searchList, String query) throws MovieException {
        return movieSearcher.search(searchList, query);
    }

    public void linkCatMov(Movie movie) throws MovieException {
        movieDAO.linkMovieWithCategories(movie);
    }

    public void deleteMovie(Movie selectedMovie) throws MovieException {
        movieDAO.deleteMovie(selectedMovie);
    }

    public void updateLastView(Movie movie, String formattedDate) throws MovieException {
        movieDAO.updateLastView(movie, formattedDate);
    }

    public List<Movie> filterMovies(double minIMDBRating, double minPersonalRating, List<String> selectedCategories) throws MovieException {
        //List<Movie> allMovies = getAllMoviesWithCategories();
        return movieFilter.filterMovies(getAllMoviesWithCategories(), minIMDBRating, minPersonalRating, selectedCategories);
    }

}
