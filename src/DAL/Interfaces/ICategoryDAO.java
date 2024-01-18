package DAL.Interfaces;

import BE.Category;
import Util.MovieException;

import java.sql.SQLException;
import java.util.Map;

public interface ICategoryDAO {

    /**
     * Retrieves all categories from the database.
     *
     * @return A map of category names to their respective IDs.
     * @throws MovieException if there is an error executing the SQL command.
     */
    Map<String, Integer> getAllCategories() throws MovieException;

    /**
     * Removes the movie from CatMovie table.
     * @param movieId
     * @throws MovieException if there is an error executing the SQL command.
     */
    void removeCategoriesFromMovie(int movieId) throws MovieException;

    /**
     * Method for adding a category to the database
     * @param category
     * @return
     * @throws MovieException if there is an error executing the SQL command.
     */
    Category addCategory(Category category) throws  MovieException;

    /**
     * Method for deleting a category from database.
     * and also removes the connections in the CatMovie table.
     * @param category
     * @throws MovieException if there is an error executing the SQL command.
     */
    void deleteCategory(Category category) throws MovieException;



}
