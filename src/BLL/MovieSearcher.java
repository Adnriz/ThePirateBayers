package BLL;

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
        return compareToMovieTitle(query, movie) ||
                compareToMoviePersonalRating(query, movie) ||
                compareToMovieIMDBRating(query, movie) ||
                compareToMovieLastView(query, movie);
    }

    private boolean compareToMovieTitle(String query, Movie movie){
        return movie.getMovieTitle().toLowerCase().contains(query.toLowerCase());
    }
    private boolean compareToMoviePersonalRating(String query, Movie movie){
        return Double.toString(movie.getPersonalRating()).contains(query);
    }
    private boolean compareToMovieIMDBRating(String query, Movie movie){
        return Double.toString(movie.getImdbRating()).contains(query);
    }
    private boolean compareToMovieLastView(String query, Movie movie){
        return movie.getLastView().contains(query);
    }

    /**
     * Takes the query and searches through the categories
     * for a match, and if it contains the search returns true.
     * @param movie
     * @param query
     * @return
     */
    private boolean matchesCategoryQuery(Movie movie, String query) {
        List<Category> categories = movie.getCategories();

        if (categories != null) {
            for (Category category : categories) {
                if (category.getName().toLowerCase().contains(query.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method uses the MatchesCategoryQuery
     * to search through a list of movies and retrieves
     * those that match the criteria of the search
     * @param searchBase
     * @param query
     * @return
     */
    public List<Movie> searchByCategory(List<Movie> searchBase, String query) {
        List<Movie> searchResult = new ArrayList<>();

        for (Movie movie : searchBase) {
            if (matchesCategoryQuery(movie, query)) {
                searchResult.add(movie);
            }
        }
        return searchResult;
    }




}
