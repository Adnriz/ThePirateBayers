package GUI.Model;

import BE.Category;
import BLL.CategoryManager;
import DAL.CategoryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CategoryModel {
    private CategoryManager categoryManager;
    private Map<String, Integer> categoryNameToIdMap;
    private ObservableList<Category> categoriesToBeViewed = FXCollections.observableArrayList();

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
            categoryNameToIdMap = categoryManager.getAllCategories();
            categoriesToBeViewed.setAll(getCategories());
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
    public void removeCategoriesFromMovie(int movieId) throws SQLException {
        categoryManager.removeCategoriesFromMovie(movieId);
    }

    public void addCategory(Category newCategory) throws SQLException {
        Category category = categoryManager.addCategory(newCategory);
        categoriesToBeViewed.add(category);

        // Refresh the list
        loadCategories();
    }
    public void deleteCategory(Category selectedCategory) throws SQLException {
        categoryManager.deleteCategory(selectedCategory);

        // Refresh the list
        loadCategories();
    }
    public ObservableList<Category> getCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : categoryNameToIdMap.entrySet()) {
            Category category = new Category(entry.getValue(), entry.getKey());
            categories.add(category);
        }

        // Sorts the list into alphabetically order.
        categories.sort(Comparator.comparing(Category::getName));

        return categories;
    }
}
