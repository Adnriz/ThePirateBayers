package DAL;

import BE.Category;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryDAO {
    private DBConnector databaseConnector;

    public CategoryDAO() throws SQLException, IOException {
        this.databaseConnector = new DBConnector();
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A map of category names to their respective IDs.
     * @throws SQLException If there is a problem with the database access.
     */
    public Map<String, Integer> getAllCategories() throws SQLException {
        Map<String, Integer> categories = new HashMap<>();
        String sql = "SELECT id, Category FROM Category;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("Category");
                int id = rs.getInt("id");
                categories.put(name, id);
            }
        }
        return categories;
    }
    public void removeCategoriesFromMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM CatMovie WHERE Movieid = ?";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieId);
            pstmt.executeUpdate();
        }
    }


}