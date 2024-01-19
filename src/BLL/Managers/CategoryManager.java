package BLL.Managers;

import BE.Category;
import DAL.DAO.CategoryDAO;
import Util.MovieException;

import java.util.Map;

public class CategoryManager {
    private final CategoryDAO categoryDAO;

    public CategoryManager() throws MovieException {categoryDAO = new CategoryDAO();
    }

    public Map<String, Integer> getAllCategories() throws MovieException {
        return categoryDAO.getAllCategories();
    }
    public void removeCategoriesFromMovie(int movieId) throws MovieException {
        categoryDAO.removeCategoriesFromMovie(movieId);
    }

    public Category addCategory(Category newCategory) throws MovieException {
        return categoryDAO.addCategory(newCategory);
    }

    public void deleteCategory(Category selectedCategory) throws MovieException {
        categoryDAO.deleteCategory(selectedCategory);
    }



}