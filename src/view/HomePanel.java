package view;

import controller.BookController;
import controller.CategoryController;
import model.Book;
import model.Category;
import model.User;
import utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Home Panel for users to browse books by category and make purchases.
 */
public class HomePanel extends JPanel {
    private BookController bookController;
    private CategoryController categoryController;
    private User loggedInUser;
    private MainFrame mainFrame; // Reference to the MainFrame

    private JTabbedPane categoryTabbedPane;
    private final int BOOKS_PER_ROW = 4;
    private final int INITIAL_ROWS_PER_CATEGORY = 1;
    private final int PAGE_SIZE = BOOKS_PER_ROW;

    private Map<Integer, Integer> categoryOffsets;

    public HomePanel(User user, MainFrame mainFrame) { // Constructor now accepts MainFrame
        this.loggedInUser = user;
        this.mainFrame = mainFrame; // Store the MainFrame reference
        bookController = new BookController();
        categoryController = new CategoryController();
        categoryOffsets = new HashMap<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        initComponents();
        loadCategoriesAndBooks();
    }

    private void initComponents() {
        JLabel headerLabel = new JLabel("Browse Books by Category", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, BorderLayout.NORTH);

        categoryTabbedPane = new JTabbedPane();
        categoryTabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        add(categoryTabbedPane, BorderLayout.CENTER);
    }

    private void loadCategoriesAndBooks() {
        List<Category> categories = categoryController.getAllCategories();
        if (categories.isEmpty()) {
            categoryTabbedPane.addTab("No Categories", new JLabel("No book categories available.", SwingConstants.CENTER));
            return;
        }

        for (Category category : categories) {
            JPanel categoryPanel = createCategoryBookDisplayPanel(category);
            JScrollPane scrollPane = new JScrollPane(categoryPanel);
            categoryTabbedPane.addTab(category.getName(), scrollPane);

            // Safely cast and store category object directly on the JScrollPane
            ((JComponent) scrollPane).putClientProperty("categoryObject", category);

            categoryOffsets.put(category.getId(), 0);
            loadBooksForCategory(category, categoryPanel, INITIAL_ROWS_PER_CATEGORY * BOOKS_PER_ROW);
        }
    }

    private JPanel createCategoryBookDisplayPanel(Category category) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel booksContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        booksContainer.setBackground(Color.WHITE);
        panel.add(booksContainer);

        JButton showMoreButton = new JButton("Show More");
        showMoreButton.setFont(new Font("Arial", Font.BOLD, 12));
        showMoreButton.setBackground(new Color(100, 149, 237)); // Light blue
        showMoreButton.setForeground(Color.WHITE);
        showMoreButton.setFocusPainted(false);
        showMoreButton.setBorder(BorderFactory.createLineBorder(new Color(80, 120, 200), 1));
        showMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        showMoreButton.addActionListener(e -> {
            loadBooksForCategory(category, panel, PAGE_SIZE);
        });

        panel.add(showMoreButton);

        // Store references to sub-components for later retrieval
        panel.putClientProperty("booksContainer", booksContainer);
        panel.putClientProperty("showMoreButton", showMoreButton);

        return panel;
    }

    /**
     * Loads books for a specific category with pagination.
     * @param category The category to load books for.
     * @param categoryPanel The JPanel representing the category's display.
     * @param limit The number of books to load in this batch.
     */
    private void loadBooksForCategory(Category category, JPanel categoryPanel, int limit) {
        // Get the current offset for this category
        int currentOffset = categoryOffsets.getOrDefault(category.getId(), 0);
        // Fetch books from the controller using pagination
        List<Book> books = bookController.getBooksByCategoryPaginated(category.getId(), currentOffset, limit);
        // Get the total count of books in this category to manage "Show More" button visibility
        int totalBooksInThisCategory = bookController.getTotalBooksInCategory(category.getId());

        // Retrieve the books container and show more button from client properties
        JPanel booksContainer = (JPanel) categoryPanel.getClientProperty("booksContainer");
        JButton showMoreButton = (JButton) categoryPanel.getClientProperty("showMoreButton");

        // If this is the first load (offset is 0), clear existing cards
        if (currentOffset == 0) {
            booksContainer.removeAll();
        }

        // Add new book cards to the container
        for (Book book : books) {
            booksContainer.add(createBookCard(book));
        }

        // Update the offset for the next load
        categoryOffsets.put(category.getId(), currentOffset + books.size());

        // Update "Show More" button visibility
        showMoreButton.setVisible(categoryOffsets.get(category.getId()) < totalBooksInThisCategory);

        // Revalidate and repaint the panel to reflect changes
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    /**
     * Creates a visual card for a single book.
     * @param book The Book object to display.
     * @return A JPanel representing the book card.
     */
    private JPanel createBookCard(Book book) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Light gray border
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(180, 280)); // Fixed size for consistent layout

        // Image Label
        JLabel imageLabel = new JLabel();
        ImageIcon icon = ImageUtils.loadImageIcon(book.getImageUrl(), 100, 150); // Load and scale image
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            // Placeholder if image fails to load or URL is empty
            imageLabel.setIcon(ImageUtils.createPlaceholderImageIcon(100, 150));
            imageLabel.setText("No Image");
            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            imageLabel.setVerticalTextPosition(SwingConstants.CENTER);
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imageLabel, BorderLayout.NORTH);

        // Details Panel (Title, Author, Price, Quantity)
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1)); // Single column layout
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel titleLabel = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setToolTipText(book.getTitle()); // Show full title on hover
        detailsPanel.add(titleLabel);

        JLabel authorLabel = new JLabel("by " + book.getAuthor());
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        detailsPanel.add(authorLabel);

        JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", book.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        detailsPanel.add(priceLabel);

        JLabel quantityLabel = new JLabel("Stock: " + book.getQuantity());
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        detailsPanel.add(quantityLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        // Button Panel (Buy or Edit/Delete)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        if ("User".equals(loggedInUser.getRole())) {
            JButton buyButton = new JButton("Buy");
            styleCardButton(buyButton, new Color(60, 179, 113)); // MediumSeaGreen
            buyButton.setEnabled(book.getQuantity() > 0); // Disable if out of stock
            buyButton.addActionListener(e -> showPurchaseDialog(book));
            buttonPanel.add(buyButton);
        } else if ("Owner".equals(loggedInUser.getRole())) {
            // For Owner, enable direct Edit/Delete from Home tab
            JButton editButton = new JButton("Edit");
            styleCardButton(editButton, new Color(30, 144, 255)); // DodgerBlue
            editButton.addActionListener(e -> {
                // Switch to Books tab and populate form for editing
                if (mainFrame != null) {
                    mainFrame.switchToTab("Books");
                    mainFrame.getBookPanel().displayBookDetails(book); // Call public method in BookPanel
                } else {
                    JOptionPane.showMessageDialog(this, "MainFrame reference not available for editing.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(editButton);

            JButton deleteButton = new JButton("Delete");
            styleCardButton(deleteButton, new Color(220, 20, 60)); // Crimson
            deleteButton.addActionListener(e -> {
                // Directly perform delete action
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete '" + book.getTitle() + "'?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookController.deleteBook(book.getId())) {
                        JOptionPane.showMessageDialog(this, "'" + book.getTitle() + "' deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshCurrentCategoryPanel(); // Refresh the home panel after deletion
                        // Also refresh the BookPanel table if it's open
                        if (mainFrame != null) {
                            mainFrame.getBookPanel().refreshBookTable();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete '" + book.getTitle() + "'. It might be referenced by purchases.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(deleteButton);
        }

        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Styles a button for use in book cards.
     * @param button The JButton to style.
     * @param bgColor The background color for the button.
     */
    private void styleCardButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
        button.setPreferredSize(new Dimension(70, 25));
    }

    /**
     * Displays a dialog for purchasing a book.
     * @param book The book to be purchased.
     */
    private void showPurchaseDialog(Book book) {
        if (book.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "This book is currently out of stock.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity for " + book.getTitle() + " (Max: " + book.getQuantity() + "):", "Purchase Book", JOptionPane.QUESTION_MESSAGE);

        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                int quantityToBuy = Integer.parseInt(quantityStr.trim());

                if (quantityToBuy <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (quantityToBuy > book.getQuantity()) {
                    JOptionPane.showMessageDialog(this, "Not enough stock. Available: " + book.getQuantity(), "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Confirm purchase with the user
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Confirm purchase of " + quantityToBuy + " x " + book.getTitle() + " for $" + book.getPrice().multiply(new BigDecimal(quantityToBuy)) + "?",
                        "Confirm Purchase", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Process the purchase through the BookController
                    if (bookController.processPurchase(book.getId(), quantityToBuy, loggedInUser.getId())) {
                        JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshCurrentCategoryPanel(); // Refresh the display after successful purchase
                    } else {
                        JOptionPane.showMessageDialog(this, "Purchase failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the currently active category panel to reflect updated book quantities
     * or newly loaded books.
     */
    private void refreshCurrentCategoryPanel() {
        int selectedIndex = categoryTabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            // Get the JScrollPane which is the component for the selected tab
            JComponent selectedTabComponent = (JComponent) categoryTabbedPane.getComponentAt(selectedIndex);

            // Retrieve the Category object directly from the JScrollPane's client property
            Category category = (Category) selectedTabComponent.getClientProperty("categoryObject");

            // Ensure we have a valid category and the component is a JScrollPane
            if (category != null && selectedTabComponent instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) selectedTabComponent;
                Component viewportView = scrollPane.getViewport().getView();

                // Ensure the viewport view is the JPanel containing the book cards
                if (viewportView instanceof JPanel) {
                    JPanel categoryPanel = (JPanel) viewportView;
                    // Reset the offset for the current category to reload from the beginning
                    categoryOffsets.put(category.getId(), 0);
                    // Reload books for this category. This will clear existing cards and add fresh ones.
                    loadBooksForCategory(category, categoryPanel, INITIAL_ROWS_PER_CATEGORY * BOOKS_PER_ROW);
                }
            }
        }
    }
}
