package BLL;

import DAL.CategoryDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class CategoryManager {
    private final CategoryDAO categoryDAO;

    public CategoryManager() throws SQLException, IOException {
        categoryDAO = new CategoryDAO();
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A map of category names and their IDs.
     * @throws SQLException If there is a problem with the database access.
     */
    public Map<String, Integer> getAllCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }
    public void removeCategoriesFromMovie(int movieId) throws SQLException {
        categoryDAO.removeCategoriesFromMovie(movieId);
    }
}