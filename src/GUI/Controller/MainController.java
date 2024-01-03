package GUI.Controller;

import BE.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private Button btnApplyFilters;

    @FXML
    private Button btnClearFilters;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewMovie;

    @FXML
    private Button btnUpdate;

    @FXML
    private ComboBox<String> cbCategory1;

    @FXML
    private ComboBox<String> cbCategory2;

    @FXML
    private ComboBox<String> cbCategory3;

    @FXML
    private Spinner<Double> spinnerIMDB;

    @FXML
    private Spinner<Double> spinnerPersonal;

    @FXML
    private TableView<Movie> tblviewMovies;

    @FXML
    private TextField txtSearch;

}
