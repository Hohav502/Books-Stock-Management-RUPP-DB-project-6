package dao;

import model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Data Access Object for Book entities.
 * Handles CRUD operations for Books in the database.
 */
public class BookDAO {

    /**
     * Adds a new book to the database.
     * @param book The Book object to add.
     * @return true if the book was added successfully, false otherwise.
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            // Handle null category_id if needed, though schema has ON DELETE SET NULL
            if (book.getCategoryId() == 0) { // Assuming 0 means no category selected or invalid
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, book.getCategoryId());
            }
            pstmt.setBigDecimal(4, book.getPrice());
            pstmt.setInt(5, book.getQuantity());
            pstmt.setString(6, book.getIsbn());
            pstmt.setDate(7, book.getPublicationDate());
            pstmt.setString(8, book.getDescription());
            pstmt.setString(9, book.getImageUrl());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a book by its ID.
     * @param id The ID of the book to retrieve.
     * @return The Book object if found, null otherwise.
     */
    public Book getBookById(int id) {
        String sql = "SELECT * FROM Books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractBookFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all books from the database.
     * @return A list of all Book objects.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books ORDER BY title";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Retrieves books by category with pagination.
     * @param categoryId The ID of the category.
     * @param offset The starting index for results.
     * @param limit The maximum number of results to return.
     * @return A list of books in the specified category.
     */
    public List<Book> getBooksByCategory(int categoryId, int offset, int limit) {
        List<Book> books = new ArrayList<>();
        // Corrected SQL for PostgreSQL: LIMIT count OFFSET offset
        String sql = "SELECT * FROM Books WHERE category_id = ? ORDER BY title LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, limit);  // The LIMIT value (count)
            pstmt.setInt(3, offset); // The OFFSET value (starting index)

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by category: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Gets the total count of books in a specific category.
     * @param categoryId The ID of the category.
     * @return The total number of books.
     */
    public int getTotalBooksInCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Books WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total books in category: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Updates an existing book in the database.
     * @param book The Book object with updated information.
     * @return true if the book was updated successfully, false otherwise.
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE Books SET title = ?, author = ?, category_id = ?, price = ?, quantity = ?, isbn = ?, publication_date = ?, description = ?, image_url = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            if (book.getCategoryId() == 0) { // Assuming 0 means no category selected or invalid
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, book.getCategoryId());
            }
            pstmt.setBigDecimal(4, book.getPrice());
            pstmt.setInt(5, book.getQuantity());
            pstmt.setString(6, book.getIsbn());
            pstmt.setDate(7, book.getPublicationDate());
            pstmt.setString(8, book.getDescription());
            pstmt.setString(9, book.getImageUrl());
            pstmt.setInt(10, book.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a book from the database by its ID.
     * @param id The ID of the book to delete.
     * @return true if the book was deleted successfully, false otherwise.
     */
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM Books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Searches for books based on a keyword in title, author, ISBN, or ID.
     * @param keyword The keyword to search for.
     * @return A list of books matching the keyword.
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM Books b LEFT JOIN Categories c ON b.category_id = c.id " +
                "WHERE b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? OR b.id LIKE ? OR c.name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            pstmt.setString(4, searchKeyword); // Search by ID as string
            pstmt.setString(5, searchKeyword); // Search by Category name

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Helper method to extract a Book object from a ResultSet.
     * @param rs The ResultSet containing book data.
     * @return A Book object.
     * @throws SQLException If a database access error occurs.
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setCategoryId(rs.getInt("category_id"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setQuantity(rs.getInt("quantity"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublicationDate(rs.getDate("publication_date"));
        book.setDescription(rs.getString("description"));
        book.setImageUrl(rs.getString("image_url"));
        return book;
    }

    /**
     * Updates the quantity of a book after a purchase.
     * @param bookId The ID of the book.
     * @param quantityChange The amount to change the quantity by (negative for purchase).
     * @return true if the quantity was updated successfully, false otherwise.
     */
    public boolean updateBookQuantity(int bookId, int quantityChange) {
        String sql = "UPDATE Books SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, bookId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book quantity: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
