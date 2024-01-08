package BLL;

import BE.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieSearcher {



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

    private boolean matchesQuery(Movie movie, String query){
        return compareToMovieTitle(query, movie) ||
              //  compareToMovieCategory(query, movie) ||
                compareToMoviePersonalRating(query, movie) ||
                compareToMovieIMDBRating(query, movie) ||
                compareToMovieLastView(query, movie);
    }

/*    private boolean compareToMovieCategory(String query, Movie movie){
        return
    }*/

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
}
