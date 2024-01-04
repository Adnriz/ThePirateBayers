package GUI.Model;

import BE.Movie;
import BLL.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MovieModel {

    private ObservableList<Movie> availableMovies;
    private MovieManager movieManager;

    public MovieModel() throws Exception{
        movieManager = new MovieManager();
        availableMovies = FXCollections.observableArrayList(movieManager.getAllMovies());
    }

    public ObservableList<Movie> getObservableMovies() {
        return availableMovies;
    }

}
