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

import java.io.IOException;
import java.sql.SQLException;
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

    public OutdatedController() throws MovieException {
        this.movieModel = movieModel;
    }
    public void setMovieModel(MovieModel movieModel) {
        this.movieModel = movieModel;
        setupList();
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

    ////////////////////////
    //// Helper Methods ////
    ////   Initialize   ////
    ////////////////////////

    /*private void setupList() {
        if (outdatedMovieView != null) {
            List<Movie> movies = movieModel.getOutdatedMoviesAndBadRatingList();


            //Making the List<Movie> to ObservableList<Movie> for the ListView
            ObservableList<String> movieNames = FXCollections.observableArrayList(
                    movies.stream().map(Movie::getMovieTitle).collect(Collectors.toList())
            );
            //Setting the movie titles in the ListView
            outdatedMovieView.setItems(movieNames);
        }
    }*/

    /**
     * Sets up the list view with movies that are outdated and/or have bad personal ratings.
     * Each item in the list view will display the movie's title, personal rating, and last viewed date.
     */
    private void setupList() {
        if (outdatedMovieView != null) {
            List<Movie> movies = movieModel.getOutdatedMoviesAndBadRatingList();

            final int maxTitleLength = 20;

            // Concatenating movie title, personal rating, and last viewed into a single string for each movie
            ObservableList<String> movieDetails = FXCollections.observableArrayList(
                    movies.stream()
                            .map(movie -> {
                                String movieTitle = movie.getMovieTitle();

                                if (movieTitle.length() > maxTitleLength){
                                    movieTitle = movieTitle.substring(0, maxTitleLength) + "...";
                                }
                                return movieTitle + " - Last viewed: " + movie.getLastView() + ", Personal rating: " + movie.getPersonalRating();
                            })
                            .collect(Collectors.toList())
            );

            // Setting the concatenated details in the ListView
            outdatedMovieView.setItems(movieDetails);
        }
    }

}
