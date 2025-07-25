package dao;

import model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category entities.
 * Handles CRUD operations for Categories in the database.
 */
public class CategoryDAO {

    /**
     * Adds a new category to the database.
     * @param category The Category object to add.
     * @return true if the category was added successfully, false otherwise.
     */
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO Categories (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, category.getName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a category by its ID.
     * @param id The ID of the category to retrieve.
     * @return The Category object if found, null otherwise.
     */
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM Categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting category by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a category by its name.
     * @param name The name of the category to retrieve.
     * @return The Category object if found, null otherwise.
     */
    public Category getCategoryByName(String name) {
        String sql = "SELECT * FROM Categories WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCategoryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting category by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all categories from the database.
     * @return A list of all Category objects.
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Updates an existing category in the database.
     * @param category The Category object with updated information.
     * @return true if the category was updated successfully, false otherwise.
     */
    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a category from the database by its ID.
     * @param id The ID of the category to delete.
     * @return true if the category was deleted successfully, false otherwise.
     */
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM Categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method to extract a Category object from a ResultSet.
     * @param rs The ResultSet containing category data.
     * @return A Category object.
     * @throws SQLException If a database access error occurs.
     */
    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        return category;
    }
}

