package dao;

import model.Purchase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Data Access Object for Purchase entities.
 * Handles CRUD operations for Purchases in the database.
 */
public class PurchaseDAO {

    /**
     * Adds a new purchase record to the database.
     * @param purchase The Purchase object to add.
     * @return true if the purchase was added successfully, false otherwise.
     */
    public boolean addPurchase(Purchase purchase) {
        String sql = "INSERT INTO Purchases (book_id, book_title, book_image, book_price, quantity, total_price, purchase_date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, purchase.getBookId());
            pstmt.setString(2, purchase.getBookTitle());
            pstmt.setString(3, purchase.getBookImage());
            pstmt.setBigDecimal(4, purchase.getBookPrice());
            pstmt.setInt(5, purchase.getQuantity());
            pstmt.setBigDecimal(6, purchase.getTotalPrice());
            pstmt.setTimestamp(7, purchase.getPurchaseDate());
            pstmt.setInt(8, purchase.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        purchase.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding purchase: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a purchase record by its ID.
     * @param id The ID of the purchase to retrieve.
     * @return The Purchase object if found, null otherwise.
     */
    public Purchase getPurchaseById(int id) {
        String sql = "SELECT * FROM Purchases WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPurchaseFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all purchase records from the database.
     * @return A list of all Purchase objects.
     */
    public List<Purchase> getAllPurchases() {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM Purchases ORDER BY purchase_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                purchases.add(extractPurchaseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all purchases: " + e.getMessage());
            e.printStackTrace();
        }
        return purchases;
    }

    /**
     * Retrieves purchase records by user ID.
     * @param userId The ID of the user.
     * @return A list of Purchase objects made by the specified user.
     */
    public List<Purchase> getPurchasesByUserId(int userId) {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM Purchases WHERE user_id = ? ORDER BY purchase_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    purchases.add(extractPurchaseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchases by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return purchases;
    }

    /**
     * Helper method to extract a Purchase object from a ResultSet.
     * @param rs The ResultSet containing purchase data.
     * @return A Purchase object.
     * @throws SQLException If a database access error occurs.
     */
    private Purchase extractPurchaseFromResultSet(ResultSet rs) throws SQLException {
        Purchase purchase = new Purchase();
        purchase.setId(rs.getInt("id"));
        purchase.setBookId(rs.getInt("book_id"));
        purchase.setBookTitle(rs.getString("book_title"));
        purchase.setBookImage(rs.getString("book_image"));
        purchase.setBookPrice(rs.getBigDecimal("book_price"));
        purchase.setQuantity(rs.getInt("quantity"));
        purchase.setTotalPrice(rs.getBigDecimal("total_price"));
        purchase.setPurchaseDate(rs.getTimestamp("purchase_date"));
        purchase.setUserId(rs.getInt("user_id"));
        return purchase;
    }
}
