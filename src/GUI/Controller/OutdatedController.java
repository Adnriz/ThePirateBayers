package GUI.Controller;

import BE.Movie;
import GUI.Model.MovieModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OutdatedController {

    public Button deleteOutdatedMovies;
    public Button ignoreMovies;
    @FXML
    private ListView<String> outdatedMovieView;
    private MovieModel movieModel;


    public OutdatedController() throws SQLException, IOException {
        this.movieModel = movieModel;
    }
    public void setMovieModel(MovieModel movieModel) {
        this.movieModel = movieModel;
        setupList();
    }

    private void setupList() {
        if (outdatedMovieView != null) {
            List<Movie> movies = movieModel.getOutdatedMoviesList();

            //Making the List<Movie> to ObservableList<Movie> for the ListView
            ObservableList<String> movieNames = FXCollections.observableArrayList(
                    movies.stream().map(Movie::getMovieTitle).collect(Collectors.toList())
            );
            //Setting the movie titles in the ListView
            outdatedMovieView.setItems(movieNames);
        }
    }
    @FXML
    private void benIgnoreMovies(ActionEvent actionEvent) {
        Stage stage = (Stage) outdatedMovieView.getScene().getWindow();
        stage.close();
    }

}
