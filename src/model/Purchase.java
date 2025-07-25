package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a Purchase record in the system.
 */
public class Purchase {
    private int id;
    private int bookId; // Foreign key to Book
    private String bookTitle; // New field for purchase history
    private String bookImage; // New field for purchase history
    private BigDecimal bookPrice; // Price at the time of purchase
    private int quantity;
    private BigDecimal totalPrice;
    private Timestamp purchaseDate; // Using Timestamp for date and time
    private int userId; // Foreign key to User who made the purchase

    // Constructors
    public Purchase() {
    }

    public Purchase(int id, int bookId, String bookTitle, String bookImage, BigDecimal bookPrice, int quantity, BigDecimal totalPrice, Timestamp purchaseDate, int userId) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookImage = bookImage;
        this.bookPrice = bookPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.userId = userId;
    }

    public Purchase(int bookId, String bookTitle, String bookImage, BigDecimal bookPrice, int quantity, BigDecimal totalPrice, Timestamp purchaseDate, int userId) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookImage = bookImage;
        this.bookPrice = bookPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public BigDecimal getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookImage='" + bookImage + '\'' +
                ", bookPrice=" + bookPrice +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", purchaseDate=" + purchaseDate +
                ", userId=" + userId +
                '}';
    }
}

