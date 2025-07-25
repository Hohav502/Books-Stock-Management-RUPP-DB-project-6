package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Handles database connection and initialization for PostgreSQL.
 */
public class DBConnection {
    // PostgreSQL database details
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/bookstore";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwer";

    static {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Please add it to your classpath.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Initializes the database schema for PostgreSQL.
     * This method should be called once when the application starts.
     * Ensure your PostgreSQL database 'bookstore' exists before running this.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Connected to PostgreSQL database. Initializing schema...");

            // Create Categories table (PostgreSQL syntax)
            stmt.execute("CREATE TABLE IF NOT EXISTS Categories (" +
                    "id SERIAL PRIMARY KEY," +  // SERIAL is PostgreSQL's auto-increment
                    "name VARCHAR(255) NOT NULL UNIQUE" +
                    ")");

            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(20) NOT NULL" +
                    ")");

            // Create Books table
            stmt.execute("CREATE TABLE IF NOT EXISTS Books (" +
                    "id SERIAL PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "author VARCHAR(255) NOT NULL," +
                    "category_id INT," +
                    "price DECIMAL(10, 2) NOT NULL," +
                    "quantity INT NOT NULL," +
                    "isbn VARCHAR(20) UNIQUE," +
                    "publication_date DATE," +
                    "description VARCHAR(1000)," +
                    "image_url VARCHAR(500)," +
                    "FOREIGN KEY (category_id) REFERENCES Categories(id) ON DELETE SET NULL" +
                    ")");

            // Create Purchases table
            stmt.execute("CREATE TABLE IF NOT EXISTS Purchases (" +
                    "id SERIAL PRIMARY KEY," +
                    "book_id INT NOT NULL," +
                    "book_title VARCHAR(255) NOT NULL," +
                    "book_image VARCHAR(500)," +
                    "book_price DECIMAL(10, 2) NOT NULL," +
                    "quantity INT NOT NULL," +
                    "total_price DECIMAL(10, 2) NOT NULL," +
                    "purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "user_id INT NOT NULL," +
                    "FOREIGN KEY (book_id) REFERENCES Books(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE" +
                    ")");

            // Insert initial data if tables are empty
            insertInitialData(conn); // Call the helper method to insert data

            System.out.println("PostgreSQL database schema initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            System.err.println("Please ensure PostgreSQL server is running and 'bookstore' database exists.");
            e.printStackTrace();
        }
    }

    /**
     * Inserts initial data into Categories, Users, and Books tables if they are empty.
     * @param conn The database connection.
     * @throws SQLException If a database access error occurs.
     */
    private static void insertInitialData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Check if categories exist, if not, insert
        ResultSet rsCategories = stmt.executeQuery("SELECT COUNT(*) FROM Categories");
        rsCategories.next();
        if (rsCategories.getInt(1) == 0) {
            stmt.execute("INSERT INTO Categories (name) VALUES ('Fiction')");
            stmt.execute("INSERT INTO Categories (name) VALUES ('Science')");
            stmt.execute("INSERT INTO Categories (name) VALUES ('History')");
            stmt.execute("INSERT INTO Categories (name) VALUES ('Programming')");
            System.out.println("Initial categories inserted.");
        }

        // Check if users exist, if not, insert
        ResultSet rsUsers = stmt.executeQuery("SELECT COUNT(*) FROM Users");
        rsUsers.next();
        if (rsUsers.getInt(1) == 0) {
            stmt.execute("INSERT INTO Users (username, password, role) VALUES ('owner', 'ownerpass', 'Owner')"); // Insecure, for demo only
            stmt.execute("INSERT INTO Users (username, password, role) VALUES ('user', 'userpass', 'User')");   // Insecure, for demo only
            System.out.println("Initial users inserted.");
        }

        // Check if books exist, if not, insert
        ResultSet rsBooks = stmt.executeQuery("SELECT COUNT(*) FROM Books");
        rsBooks.next();
        if (rsBooks.getInt(1) == 0) {
            // Get category IDs
            int fictionId = -1, programmingId = -1, scienceId = -1, historyId = -1;
            ResultSet rsCat = stmt.executeQuery("SELECT id, name FROM Categories");
            while(rsCat.next()) {
                String catName = rsCat.getString("name");
                if (catName.equals("Fiction")) fictionId = rsCat.getInt("id");
                if (catName.equals("Programming")) programmingId = rsCat.getInt("id");
                if (catName.equals("Science")) scienceId = rsCat.getInt("id");
                if (catName.equals("History")) historyId = rsCat.getInt("id");
            }

            if (fictionId != -1) {
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('The Great Gatsby', 'F. Scott Fitzgerald', " + fictionId + ", 12.99, 50, '978-0743273565', '1925-04-10', 'A classic novel of the Jazz Age.', 'https://placehold.co/100x150/000/FFF?text=Gatsby')");
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('To Kill a Mockingbird', 'Harper Lee', " + fictionId + ", 10.50, 45, '978-0446310789', '1960-07-11', 'A powerful story about racial injustice.', 'https://placehold.co/100x150/000/FFF?text=Mockingbird')");
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('1984', 'George Orwell', " + fictionId + ", 9.75, 60, '978-0451524935', '1949-06-08', 'Dystopian social science fiction novel.', 'https://placehold.co/100x150/000/FFF?text=1984')");
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('Pride and Prejudice', 'Jane Austen', " + fictionId + ", 8.99, 55, '978-0141439518', '1813-01-28', 'A romantic novel of manners.', 'https://placehold.co/100x150/000/FFF?text=Pride')");
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('The Hobbit', 'J.R.R. Tolkien', " + fictionId + ", 14.25, 70, '978-0345339683', '1937-09-21', 'A fantasy novel and prelude to The Lord of the Rings.', 'https://placehold.co/100x150/000/FFF?text=Hobbit')");
            }
            if (programmingId != -1) {
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('Clean Code', 'Robert C. Martin', " + programmingId + ", 35.00, 30, '978-0132350884', '2008-08-01', 'A handbook of agile software craftsmanship.', 'https://placehold.co/100x150/000/FFF?text=CleanCode')");
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('Effective Java', 'Joshua Bloch', " + programmingId + ", 42.00, 25, '978-0321356680', '2008-05-28', 'Programming practices for the Java platform.', 'https://placehold.co/100x150/000/FFF?text=EffectiveJava')");
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('The Pragmatic Programmer', 'Andrew Hunt, David Thomas', " + programmingId + ", 30.00, 40, '978-0201616224', '1999-10-20', 'From journeyman to master.', 'https://placehold.co/100x150/000/FFF?text=Pragmatic')");
            }
            if (scienceId != -1) {
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('Cosmos', 'Carl Sagan', " + scienceId + ", 15.99, 35, '978-0345539434', '1980-09-01', 'A personal journey into the universe.', 'https://placehold.co/100x150/000/FFF?text=Cosmos')");
            }
            if (historyId != -1) {
                stmt.execute("INSERT INTO Books (title, author, category_id, price, quantity, isbn, publication_date, description, image_url) VALUES " +
                        "('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', " + historyId + ", 20.00, 40, '978-0062316097', '2015-02-10', 'A global bestseller about the history of our species.', 'https://placehold.co/100x150/000/FFF?text=Sapiens')");
            }
            System.out.println("Initial books inserted.");
        }
    }
}