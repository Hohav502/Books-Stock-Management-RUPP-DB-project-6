package model;

import java.math.BigDecimal;
import java.util.Date;

public class Book {
    private int id;
    private String title;
    private String author;
    private int categoryId; // Foreign key to Category
    private BigDecimal price;
    private int quantity;
    private String isbn;
    private Date publicationDate;
    private String description;
    private String imageUrl;

    // No-argument constructor
    public Book() {
    }

    // All-argument constructor
    public Book(int id, String title, String author, int categoryId, BigDecimal price, int quantity,
                String isbn, Date publicationDate, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Constructor without ID (for inserting new books)
    public Book(String title, String author, int categoryId, BigDecimal price, int quantity,
                String isbn, Date publicationDate, String description, String imageUrl) {
        this.title = title;
        this.author = author;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public java.sql.Date getPublicationDate() {
        return (java.sql.Date) publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}