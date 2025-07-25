package controller;

import dao.CategoryDAO;
import model.Category;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

/**
 * Controller for managing Category-related operations.
 * Acts as an intermediary between the CategoryPanel (view) and CategoryDAO (model/data).
 */
public class CategoryController {
    private CategoryDAO categoryDAO;

    public CategoryController() {
        this.categoryDAO = new CategoryDAO();
    }

    /**
     * Populates the category table model with data from the database.
     * @param tableModel The DefaultTableModel to populate.
     */
    public void populateCategoryTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing data
        List<Category> categories = categoryDAO.getAllCategories();

        for (Category category : categories) {
            Vector<Object> row = new Vector<>();
            row.add(category.getId());
            row.add(category.getName());
            tableModel.addRow(row);
        }
    }

    /**
     * Populates a JComboBox model with all categories.
     * @param comboBoxModel The DefaultComboBoxModel to populate.
     */
    public void populateCategoryComboBox(DefaultComboBoxModel<Category> comboBoxModel) {
        comboBoxModel.removeAllElements(); // Clear existing items
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            comboBoxModel.addElement(category);
        }
    }

    /**
     * Adds a new category to the system.
     * @param name The name of the category.
     * @return true if category was added, false otherwise.
     */
    public boolean addCategory(String name) {
        Category category = new Category(name);
        return categoryDAO.addCategory(category);
    }

    /**
     * Updates an existing category.
     * @param id The ID of the category to update.
     * @param name The new name for the category.
     * @return true if category was updated, false otherwise.
     */
    public boolean updateCategory(int id, String name) {
        Category category = new Category(id, name);
        return categoryDAO.updateCategory(category);
    }

    /**
     * Deletes a category by its ID.
     * @param id The ID of the category to delete.
     * @return true if category was deleted, false otherwise.
     */
    public boolean deleteCategory(int id) {
        return categoryDAO.deleteCategory(id);
    }

    /**
     * Retrieves a category by its name.
     * @param name The name of the category.
     * @return The Category object, or null if not found.
     */
    public Category getCategoryByName(String name) {
        return categoryDAO.getCategoryByName(name);
    }

    /**
     * Retrieves a category by its ID.
     * @param id The ID of the category.
     * @return The Category object, or null if not found.
     */
    public Category getCategoryById(int id) {
        return categoryDAO.getCategoryById(id);
    }

    /**
     * Retrieves all categories.
     * @return A list of all Category objects.
     */
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
}
