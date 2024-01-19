package BLL.Util;

import BE.Category;
import BE.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieSearcher {

    /**
     * If the string matches the movie objects properties.
     * then it gets added to the searchResult
     * @param searchBase is a movie list
     * @param query is a string from the GUI.
     * @return searchResult
     */

    public List<Movie> search(List<Movie> searchBase, String query) {
        List<Movie> searchResult = new ArrayList<>();

        for (Movie movie : searchBase){
            if (matchesQuery(movie,query))
            {
                searchResult.add(movie);
            }
        }
        return searchResult;
    }

    ////////////////////////
    //// Helper Methods ////
    ////     search     ////
    ////////////////////////

    /**
     * This compares the query to the move object
     * by calling methods that uses a getter,
     * and formats the query and return true or false if it contains
     * the searched properties.
     * @param movie
     * @param query
     * @return
     */
    private boolean matchesQuery(Movie movie, String query){
        return  compareToMovieTitle(query, movie) ||
                compareToMoviePersonalRating(query, movie) ||
                compareToMovieIMDBRating(query, movie) ||
                compareToMovieLastView(query, movie);
    }

    // Methods checking for matches in the movie list vs the search query.

    private boolean compareToMovieTitle(String query, Movie movie) {
        String title = movie.getMovieTitle();

        if (title == null || title.isEmpty()) {
            return false;
        }
        return title.toLowerCase().contains(query.toLowerCase());
    }

    private boolean compareToMoviePersonalRating(String query, Movie movie) {
        Double personalRating = movie.getPersonalRating();

        if (personalRating == null) {
            return false;
        }
        return Double.toString(personalRating).contains(query);
    }

    private boolean compareToMovieIMDBRating(String query, Movie movie) {
        Double imdbRating = movie.getImdbRating();

        if (imdbRating == null) {
            return false;
        }
        return Double.toString(imdbRating).contains(query);
    }

    private boolean compareToMovieLastView(String query, Movie movie) {
        String lastView = movie.getLastView();

        if (lastView == null || lastView.isEmpty()) {
            return false;
        }
        return lastView.contains(query);
    }

}
