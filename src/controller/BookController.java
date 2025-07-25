package controller;

import dao.BookDAO;
import dao.CategoryDAO;
import dao.PurchaseDAO;
import model.Book;
import model.Category;
import model.Purchase;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

public class BookController {
    private BookDAO bookDAO;
    private CategoryDAO categoryDAO;
    private PurchaseDAO purchaseDAO;

    public BookController() {
        this.bookDAO = new BookDAO();
        this.categoryDAO = new CategoryDAO();
        this.purchaseDAO = new PurchaseDAO();
    }

    public void populateBookTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();

        for (Book book : books) {
            Vector<Object> row = new Vector<>();
            row.add(book.getId());
            row.add(book.getTitle());
            row.add(book.getAuthor());

            Category category = categoryDAO.getCategoryById(book.getCategoryId());
            row.add(category != null ? category.getName() : "N/A");

            row.add(book.getPrice());
            row.add(book.getQuantity());
            row.add(book.getIsbn());
            row.add(book.getPublicationDate());
            row.add(book.getDescription());
            row.add(book.getImageUrl());

            tableModel.addRow(row);
        }
    }

    public boolean addBook(String title, String author, String categoryName, BigDecimal price, int quantity,
                           String isbn, Date publicationDate, String description, String imageUrl) {

        Category category = categoryDAO.getCategoryByName(categoryName);
        int categoryId = (category != null) ? category.getId() : 0;

        // ✅ This constructor must exist in model.Book
        Book book = new Book(title, author, categoryId, price, quantity, isbn, publicationDate, description, imageUrl);
        return bookDAO.addBook(book);
    }

    public boolean updateBook(int id, String title, String author, String categoryName, BigDecimal price, int quantity,
                              String isbn, Date publicationDate, String description, String imageUrl) {

        Category category = categoryDAO.getCategoryByName(categoryName);
        int categoryId = (category != null) ? category.getId() : 0;

        Book book = new Book(id, title, author, categoryId, price, quantity, isbn, publicationDate, description, imageUrl);
        return bookDAO.updateBook(book);
    }

    public boolean deleteBook(int id) {
        return bookDAO.deleteBook(id);
    }

    public void searchBooks(DefaultTableModel tableModel, String keyword) {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.searchBooks(keyword);

        for (Book book : books) {
            Vector<Object> row = new Vector<>();
            row.add(book.getId());
            row.add(book.getTitle());
            row.add(book.getAuthor());

            Category category = categoryDAO.getCategoryById(book.getCategoryId());
            row.add(category != null ? category.getName() : "N/A");

            row.add(book.getPrice());
            row.add(book.getQuantity());
            row.add(book.getIsbn());
            row.add(book.getPublicationDate());
            row.add(book.getDescription());
            row.add(book.getImageUrl());

            tableModel.addRow(row);
        }
    }

    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    public List<Book> getBooksByCategoryPaginated(int categoryId, int offset, int limit) {
        return bookDAO.getBooksByCategory(categoryId, offset, limit);
    }

    public int getTotalBooksInCategory(int categoryId) {
        return bookDAO.getTotalBooksInCategory(categoryId);
    }

    public boolean processPurchase(int bookId, int quantity, int userId) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null || book.getQuantity() < quantity) {
            System.err.println("Purchase failed: Book not found or insufficient stock.");
            return false;
        }

        BigDecimal itemPrice = book.getPrice();
        BigDecimal totalPrice = itemPrice.multiply(new BigDecimal(quantity));

        Purchase purchase = new Purchase(
                book.getId(),
                book.getTitle(),
                book.getImageUrl(),
                itemPrice,
                quantity,
                totalPrice,
                new Timestamp(System.currentTimeMillis()),
                userId
        );

        boolean purchaseAdded = purchaseDAO.addPurchase(purchase);
        if (purchaseAdded) {
            boolean quantityUpdated = bookDAO.updateBookQuantity(bookId, -quantity);
            if (quantityUpdated) {
                return true;
            } else {
                System.err.println("Warning: Purchase recorded but book quantity update failed.");
                return false;
            }
        }
        return false;
    }
}
