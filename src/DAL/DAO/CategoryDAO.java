package DAL.DAO;

import BE.Category;
import DAL.DBConnector;
import DAL.Interfaces.ICategoryDAO;
import Util.MovieException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryDAO implements ICategoryDAO {
    private DBConnector databaseConnector = DBConnector.getInstance();

    public CategoryDAO() throws MovieException {
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A map of category names to their respective IDs.
     * @throws MovieException if there is an error executing the SQL command.
     */
    @Override
    public Map<String, Integer> getAllCategories() throws MovieException {
        Map<String, Integer> categories = new HashMap<>();
        String sql = "SELECT id, Category FROM Category;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery())
        {

            while (rs.next()) {
                String name = rs.getString("Category");
                int id = rs.getInt("id");
                categories.put(name, id);
            }
            return categories;
        }catch (SQLException ex){
            throw new MovieException("Could not get categories from database", ex);
        }

    }

    /**
     * Removes the movie from CatMovie table.
     * @param movieId
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public void removeCategoriesFromMovie(int movieId) throws MovieException {
        String sql = "DELETE FROM CatMovie WHERE Movieid = ?";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, movieId);
            pstmt.executeUpdate();
        } catch (SQLException ex){
            throw new MovieException("Could not delete movie from database", ex);
        }
    }

    /**
     * Method for adding a category to the database
     * @param category
     * @return category
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public Category addCategory(Category category) throws  MovieException{
        String sql = "INSERT INTO Category (Category) VALUES (?)";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, category.getName());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
            return category;
        }catch (SQLException ex){
            throw new MovieException("Could not add category to database", ex);
        }

    }

    /**
     * Method for deleting a category from database.
     * and also removes the connections in the CatMovie table.
     * @param category
     * @throws MovieException if there is an error executing the SQL commands.
     */
    @Override
    public void deleteCategory(Category category) throws MovieException
    {
        String deleteCatMovieSQL = "DELETE FROM CatMovie WHERE Categoryid = ?";
        String deleteCategorySQL = "DELETE FROM Category WHERE ID = ?";

        try (Connection conn = databaseConnector.getConnection()){

            // Delete referenced entries from the CatMovie table
            deleteReferences(conn, deleteCatMovieSQL, category.getId());

            // Delete category from the Category table
            deleteTheCategory(conn, deleteCategorySQL, category.getId());

        } catch (SQLException ex){
            throw new MovieException("Could not delete category from database", ex);
        }
    }

    ////////////////////////
    //// Helper Methods ////
    //// deleteCategory ////
    ////////////////////////

    private void deleteReferences(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private void deleteTheCategory(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

}