package GUI.Controller;

import BE.Movie;
import GUI.Model.MovieModel;
import Util.MovieException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class OutdatedController {
    @FXML
    public Button ignoreMovies;
    @FXML
    private ListView<String> outdatedMovieView;
    private MovieModel movieModel;

    ////////////////////////
    ////   Initialize   ////
    // OutdatedController //
    ////////////////////////

    public OutdatedController(){
    }

    public void setMovieModel(MovieModel movieModel) {
        this.movieModel = movieModel;
    }

    /**
     * Sets up the list view with movies that are outdated and/or have bad personal ratings.
     * Each item in the list view will display the movie's title, personal rating, and last viewed date.
     */
    public void setOutdatedMovies(List<Movie> outdatedMovies) {
        final int maxTitleLength = 20;

        ObservableList<String> movieDetails = FXCollections.observableArrayList(
                outdatedMovies.stream()
                        .map(movie -> {
                            String movieTitle = movie.getMovieTitle();
                            if (movieTitle.length() > maxTitleLength) {
                                movieTitle = movieTitle.substring(0, maxTitleLength) + "...";
                            }
                            return movieTitle + " - Last viewed: " + movie.getLastView() + ", Personal rating: " + movie.getPersonalRating();
                        })
                        .collect(Collectors.toList())
        );

        outdatedMovieView.setItems(movieDetails);
    }

    ////////////////////////
    ////      Manage    ////
    ///  Outdated Movies ///
    ////////////////////////

    @FXML
    private void onIgnoreMovies() {
        Stage stage = (Stage) outdatedMovieView.getScene().getWindow();
        stage.close();
    }
}
