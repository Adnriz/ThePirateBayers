package BLL;

import BE.Category;
import BE.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class MovieFilter {

    /**
     * Filters a list of movies based on specified criteria.
     *
     * @param movies The list of movies to be filtered.
     * @param minIMDBRating The minimum IMDb rating to filter by.
     * @param minPersonalRating The minimum personal rating to filter by.
     * @param selectedCategories The list of selected categories to filter by.
     * @return A list of movies that meet the specified criteria.
     */
    public List<Movie> filterMovies(List<Movie> movies, double minIMDBRating, double minPersonalRating, List<String> selectedCategories) {
        List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : movies) {
            if (meetsRatingCriteria(movie, minIMDBRating, minPersonalRating) && meetsCategoryCriteria(movie, selectedCategories)) {
                filteredMovies.add(movie);
            }
        }

        return filteredMovies;
    }

    private boolean meetsRatingCriteria(Movie movie, double minIMDBRating, double minPersonalRating) {
        return movie.getImdbRating() >= minIMDBRating && movie.getPersonalRating() >= minPersonalRating;
    }

    private boolean meetsCategoryCriteria(Movie movie, List<String> selectedCategories) {
        if (selectedCategories.isEmpty()) return true;
        List<String> movieCategories = movie.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        return movieCategories.containsAll(selectedCategories);
    }
}