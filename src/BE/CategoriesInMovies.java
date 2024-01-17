package BE;

public class CategoriesInMovies {

    private int id;
    private int categoryId;
    private int movieId;


    public CategoriesInMovies(int id, int categoryId, int movieId) {
        this.id = id;
        this.categoryId = categoryId;
        this.movieId = movieId;
    }

    public CategoriesInMovies(int categoryId, int movieId) {
        this.categoryId = categoryId;
        this.movieId = movieId;
    }

    public CategoriesInMovies() {
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getMovieId() {
        return movieId;
    }


}
