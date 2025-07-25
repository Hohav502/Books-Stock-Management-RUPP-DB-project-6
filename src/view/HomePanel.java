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
// ... [Package and imports remain unchanged]

public class HomePanel extends JPanel {
    private BookController bookController;
    private CategoryController categoryController;
    private User loggedInUser;

    private JTabbedPane categoryTabbedPane;
    private final int BOOKS_PER_ROW = 4;
    private final int INITIAL_ROWS_PER_CATEGORY = 1;
    private final int PAGE_SIZE = BOOKS_PER_ROW;

    private Map<Integer, Integer> categoryOffsets;

    public HomePanel(User user) {
        this.loggedInUser = user;
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

            // FIX: Safely cast and store category object
            Component tabComponent = categoryTabbedPane.getComponentAt(categoryTabbedPane.getTabCount() - 1);
            if (tabComponent instanceof JComponent) {
                ((JComponent) tabComponent).putClientProperty("categoryObject", category);
            }

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
        showMoreButton.setBackground(new Color(100, 149, 237));
        showMoreButton.setForeground(Color.WHITE);
        showMoreButton.setFocusPainted(false);
        showMoreButton.setBorder(BorderFactory.createLineBorder(new Color(80, 120, 200), 1));
        showMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        showMoreButton.addActionListener(e -> {
            loadBooksForCategory(category, panel, PAGE_SIZE);
        });

        panel.add(showMoreButton);

        panel.putClientProperty("booksContainer", booksContainer);
        panel.putClientProperty("showMoreButton", showMoreButton);

        return panel;
    }

    private void loadBooksForCategory(Category category, JPanel categoryPanel, int limit) {
        int currentOffset = categoryOffsets.getOrDefault(category.getId(), 0);
        List<Book> books = bookController.getBooksByCategoryPaginated(category.getId(), currentOffset, limit);
        int totalBooksInThisCategory = bookController.getTotalBooksInCategory(category.getId());

        JPanel booksContainer = (JPanel) categoryPanel.getClientProperty("booksContainer");
        JButton showMoreButton = (JButton) categoryPanel.getClientProperty("showMoreButton");

        if (currentOffset == 0) {
            booksContainer.removeAll();
        }

        for (Book book : books) {
            booksContainer.add(createBookCard(book));
        }

        categoryOffsets.put(category.getId(), currentOffset + books.size());

        showMoreButton.setVisible(categoryOffsets.get(category.getId()) < totalBooksInThisCategory);

        categoryPanel.revalidate();
        categoryPanel.repaint();
    }

    private JPanel createBookCard(Book book) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(180, 280));

        JLabel imageLabel = new JLabel();
        ImageIcon icon = ImageUtils.loadImageIcon(book.getImageUrl(), 100, 150);
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setIcon(ImageUtils.createPlaceholderImageIcon(100, 150));
            imageLabel.setText("No Image");
            imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            imageLabel.setVerticalTextPosition(SwingConstants.CENTER);
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imageLabel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel titleLabel = new JLabel("<html><b>" + book.getTitle() + "</b></html>");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setToolTipText(book.getTitle());
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        if ("User".equals(loggedInUser.getRole())) {
            JButton buyButton = new JButton("Buy");
            styleCardButton(buyButton, new Color(60, 179, 113));
            buyButton.setEnabled(book.getQuantity() > 0);
            buyButton.addActionListener(e -> showPurchaseDialog(book));
            buttonPanel.add(buyButton);
        } else if ("Owner".equals(loggedInUser.getRole())) {
            JButton editButton = new JButton("Edit");
            styleCardButton(editButton, new Color(30, 144, 255));
            editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Owner: Edit functionality for " + book.getTitle()));
            buttonPanel.add(editButton);

            JButton deleteButton = new JButton("Delete");
            styleCardButton(deleteButton, new Color(220, 20, 60));
            deleteButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Owner: Delete functionality for " + book.getTitle()));
            buttonPanel.add(deleteButton);
        }

        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private void styleCardButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
        button.setPreferredSize(new Dimension(70, 25));
    }

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

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Confirm purchase of " + quantityToBuy + " x " + book.getTitle() + " for $" + book.getPrice().multiply(new BigDecimal(quantityToBuy)) + "?",
                        "Confirm Purchase", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookController.processPurchase(book.getId(), quantityToBuy, loggedInUser.getId())) {
                        JOptionPane.showMessageDialog(this, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshCurrentCategoryPanel();
                    } else {
                        JOptionPane.showMessageDialog(this, "Purchase failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCurrentCategoryPanel() {
        int selectedIndex = categoryTabbedPane.getSelectedIndex();
        if (selectedIndex != -1) {
            Component selectedComponent = categoryTabbedPane.getComponentAt(selectedIndex);
            if (selectedComponent instanceof JComponent) {
                Category category = (Category) ((JComponent) selectedComponent).getClientProperty("categoryObject");

                if (selectedComponent instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) selectedComponent;
                    Component viewportView = scrollPane.getViewport().getView();
                    if (viewportView instanceof JPanel) {
                        JPanel categoryPanel = (JPanel) viewportView;
                        categoryOffsets.put(category.getId(), 0);
                        loadBooksForCategory(category, categoryPanel, INITIAL_ROWS_PER_CATEGORY * BOOKS_PER_ROW);
                    }
                }
            }
        }
    }
}
