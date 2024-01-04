package BE;

public class Movie {
    private int id;
    private String MovieTitle;
    private double PersonalRating;
    private double ImdbRating;
    private String filePath;
    private String lastView;

    public Movie(String movieTitle, double personalRating, double imdbRating, String filePath, String lastView, int id) {
        MovieTitle = movieTitle;
        PersonalRating = personalRating;
        ImdbRating = imdbRating;
        this.filePath = filePath;
        this.lastView = lastView;
        this.id = id;
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
}
