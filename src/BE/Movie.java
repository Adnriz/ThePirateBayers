package BE;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String MovieTitle;
    private double PersonalRating;
    private double ImdbRating;
    private String filePath;
    private String lastView;
    private List<Category> categories;

    public Movie(String movieTitle, double personalRating, double imdbRating, String filePath, String lastView, int id) {

        setMovieTitle(movieTitle);
        setPersonalRating(personalRating);
        setImdbRating(imdbRating);
        setFilePath(filePath);
        setLastView(lastView);
        setId(id);

        setCategories(new ArrayList<>());

    }

    public Movie() {}

    public String getMovieTitle() {
        return MovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        MovieTitle = movieTitle;
    }

    public double getPersonalRating() {
        return PersonalRating;
    }

    public void setPersonalRating(double personalRating) {
        PersonalRating = personalRating;
    }

    public double getImdbRating() {
        return ImdbRating;
    }

    public void setImdbRating(double imdbRating) {
        ImdbRating = imdbRating;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLastView() {
        return lastView;
    }

    public void setLastView(String lastView) {
        this.lastView = lastView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    public void addCategory(Category category) {
        categories.add(category);
    }

}
