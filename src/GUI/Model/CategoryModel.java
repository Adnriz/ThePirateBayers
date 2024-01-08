package GUI.Model;

import BLL.CategoryManager;
import DAL.CategoryDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CategoryModel {
    private CategoryManager categoryManager;
    private Map<String, Integer> categoryNameToIdMap;

    public CategoryModel() {
        try {
            this.categoryManager = new CategoryManager();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        loadCategories();
    }

    /**
     * Loads category names and their IDs from the database.
     */
    private void loadCategories() {
        try {
            this.categoryNameToIdMap = categoryManager.getAllCategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the ID associated with a given category name.
     *
     * @param categoryName The name of the category.
     * @return The ID of the category, or -1 if not found.
     */
    public int getCategoryIDFromName(String categoryName) {
        return categoryNameToIdMap.getOrDefault(categoryName, -1);
    }
}
