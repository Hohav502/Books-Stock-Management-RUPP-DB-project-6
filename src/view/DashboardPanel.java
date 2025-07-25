package view;
import dao.BookDAO;
import dao.PurchaseDAO;
import dao.UserDAO;
import model.Book;
import model.Purchase;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel to display a dashboard with summary information.
 */
public class DashboardPanel extends JPanel {
    private JLabel totalBooksLabel;
    private JLabel totalCategoriesLabel;
    private JLabel totalUsersLabel;
    private JLabel totalSalesLabel;
    private JLabel lowStockBooksLabel;
    private User loggedInUser;

    private BookDAO bookDAO;
    private PurchaseDAO purchaseDAO;
    private UserDAO userDAO;

    public DashboardPanel(User user) {
        this.loggedInUser = user;
        bookDAO = new BookDAO();
        purchaseDAO = new PurchaseDAO();
        userDAO = new UserDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getUsername() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        statsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Key Statistics"));
        statsPanel.setBackground(Color.WHITE);

        totalBooksLabel = createStatLabel("Total Books: ");
        totalCategoriesLabel = createStatLabel("Total Categories: ");
        totalUsersLabel = createStatLabel("Total Users: ");
        totalSalesLabel = createStatLabel("Total Sales Revenue: ");
        lowStockBooksLabel = createStatLabel("Low Stock Books (<10): ");

        statsPanel.add(totalBooksLabel);
        statsPanel.add(totalCategoriesLabel);
        if ("Owner".equals(loggedInUser.getRole())) { // Changed from "Admin" to "Owner"
            statsPanel.add(totalUsersLabel);
        } else {
            statsPanel.add(new JLabel()); // Placeholder for non-owner
        }
        statsPanel.add(totalSalesLabel);
        statsPanel.add(lowStockBooksLabel);
        statsPanel.add(new JLabel()); // Placeholder

        add(statsPanel, BorderLayout.CENTER);
    }

    private JLabel createStatLabel(String prefix) {
        JLabel label = new JLabel(prefix + " N/A");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        label.setBackground(new Color(230, 240, 250)); // Light blue background
        label.setOpaque(true);
        return label;
    }

    private void loadDashboardData() {
        // Total Books
        List<Book> allBooks = bookDAO.getAllBooks();
        totalBooksLabel.setText("Total Books: " + allBooks.size());

        // Total Categories
        totalCategoriesLabel.setText("Total Categories: " + new dao.CategoryDAO().getAllCategories().size());

        // Total Users (only for Owner)
        if ("Owner".equals(loggedInUser.getRole())) {
            totalUsersLabel.setText("Total Users: " + userDAO.getAllUsers().size());
        }

        // Total Sales Revenue
        List<Purchase> allPurchases = purchaseDAO.getAllPurchases();
        BigDecimal totalRevenue = allPurchases.stream()
                .map(Purchase::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSalesLabel.setText("Total Sales Revenue: $" + String.format("%.2f", totalRevenue));

        // Low Stock Books
        long lowStockCount = allBooks.stream()
                .filter(book -> book.getQuantity() < 10)
                .count();
        lowStockBooksLabel.setText("Low Stock Books (<10): " + lowStockCount);
    }
}

