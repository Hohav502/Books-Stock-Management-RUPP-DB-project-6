package view;

import controller.BookController;
import dao.CategoryDAO;
import model.Book;
import model.Category;
import utils.ImageUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * Panel for searching books and displaying results.
 */
public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JTable searchResultsTable;
    private DefaultTableModel tableModel;
    private BookController bookController;
    private CategoryDAO categoryDAO; // To get category names

    public SearchPanel() {
        bookController = new BookController();
        categoryDAO = new CategoryDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        setupTable();
        addListeners();
    }

    private void initComponents() {
        // Search Panel at the top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel searchLabel = new JLabel("Search by Title, Author, ISBN, or Category:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField = new JTextField(30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Table for search results
        String[] columnNames = {"ID", "Title", "Author", "Category", "Price", "Quantity", "ISBN", "Pub. Date", "Description", "Image URL"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        searchResultsTable = new JTable(tableModel);
        searchResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchResultsTable.setRowHeight(25);
        searchResultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Book details display at the bottom
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Book Details"));
        detailsPanel.setPreferredSize(new Dimension(800, 200));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Title:");
        JLabel authorLabel = new JLabel("Author:");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel priceLabel = new JLabel("Price:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JLabel isbnLabel = new JLabel("ISBN:");
        JLabel pubDateLabel = new JLabel("Pub. Date:");
        JLabel descriptionLabel = new JLabel("Description:");

        // Labels to display actual data
        JLabel displayTitle = new JLabel();
        JLabel displayAuthor = new JLabel();
        JLabel displayCategory = new JLabel();
        JLabel displayPrice = new JLabel();
        JLabel displayQuantity = new JLabel();
        JLabel displayIsbn = new JLabel();
        JLabel displayPubDate = new JLabel();
        JTextArea displayDescription = new JTextArea(3, 20);
        displayDescription.setLineWrap(true);
        displayDescription.setWrapStyleWord(true);
        displayDescription.setEditable(false);
        displayDescription.setBackground(infoPanel.getBackground());

        infoPanel.add(titleLabel); infoPanel.add(displayTitle);
        infoPanel.add(authorLabel); infoPanel.add(displayAuthor);
        infoPanel.add(categoryLabel); infoPanel.add(displayCategory);
        infoPanel.add(priceLabel); infoPanel.add(displayPrice);
        infoPanel.add(quantityLabel); infoPanel.add(displayQuantity);
        infoPanel.add(isbnLabel); infoPanel.add(displayIsbn);
        infoPanel.add(pubDateLabel); infoPanel.add(displayPubDate);
        infoPanel.add(descriptionLabel); infoPanel.add(new JScrollPane(displayDescription));

        detailsPanel.add(infoPanel, BorderLayout.CENTER);

        JLabel imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(100, 150));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        detailsPanel.add(imagePreviewLabel, BorderLayout.EAST);

        add(detailsPanel, BorderLayout.SOUTH);

        // Store references to display labels for easy update
        searchResultsTable.putClientProperty("displayTitle", displayTitle);
        searchResultsTable.putClientProperty("displayAuthor", displayAuthor);
        searchResultsTable.putClientProperty("displayCategory", displayCategory);
        searchResultsTable.putClientProperty("displayPrice", displayPrice);
        searchResultsTable.putClientProperty("displayQuantity", displayQuantity);
        searchResultsTable.putClientProperty("displayIsbn", displayIsbn);
        searchResultsTable.putClientProperty("displayPubDate", displayPubDate);
        searchResultsTable.putClientProperty("displayDescription", displayDescription);
        searchResultsTable.putClientProperty("imagePreviewLabel", imagePreviewLabel);
    }

    private void setupTable() {
        searchResultsTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
        searchResultsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Title
        searchResultsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Author
        searchResultsTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Category
        searchResultsTable.getColumnModel().getColumn(4).setPreferredWidth(50);  // Price
        searchResultsTable.getColumnModel().getColumn(5).setPreferredWidth(50);  // Quantity
        searchResultsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // ISBN
        searchResultsTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Pub. Date
        searchResultsTable.getColumnModel().getColumn(8).setPreferredWidth(200); // Description
        searchResultsTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Image URL (can be hidden if not needed)
    }

    private void addListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchResultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = searchResultsTable.getSelectedRow();
                if (selectedRow != -1) {
                    displaySelectedBookDetails(selectedRow);
                } else {
                    clearBookDetails();
                }
            }
        });
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search keyword.", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        bookController.searchBooks(tableModel, keyword);
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No books found matching your criteria.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }
        clearBookDetails(); // Clear details when new search is performed
    }

    private void displaySelectedBookDetails(int selectedRow) {
        JLabel displayTitle = (JLabel) searchResultsTable.getClientProperty("displayTitle");
        JLabel displayAuthor = (JLabel) searchResultsTable.getClientProperty("displayAuthor");
        JLabel displayCategory = (JLabel) searchResultsTable.getClientProperty("displayCategory");
        JLabel displayPrice = (JLabel) searchResultsTable.getClientProperty("displayPrice");
        JLabel displayQuantity = (JLabel) searchResultsTable.getClientProperty("displayQuantity");
        JLabel displayIsbn = (JLabel) searchResultsTable.getClientProperty("displayIsbn");
        JLabel displayPubDate = (JLabel) searchResultsTable.getClientProperty("displayPubDate");
        JTextArea displayDescription = (JTextArea) searchResultsTable.getClientProperty("displayDescription");
        JLabel imagePreviewLabel = (JLabel) searchResultsTable.getClientProperty("imagePreviewLabel");

        displayTitle.setText(tableModel.getValueAt(selectedRow, 1).toString());
        displayAuthor.setText(tableModel.getValueAt(selectedRow, 2).toString());
        displayCategory.setText(tableModel.getValueAt(selectedRow, 3).toString());
        displayPrice.setText(tableModel.getValueAt(selectedRow, 4).toString());
        displayQuantity.setText(tableModel.getValueAt(selectedRow, 5).toString());
        displayIsbn.setText(tableModel.getValueAt(selectedRow, 6) != null ? tableModel.getValueAt(selectedRow, 6).toString() : "N/A");
        displayPubDate.setText(tableModel.getValueAt(selectedRow, 7) != null ? tableModel.getValueAt(selectedRow, 7).toString() : "N/A");
        displayDescription.setText(tableModel.getValueAt(selectedRow, 8) != null ? tableModel.getValueAt(selectedRow, 8).toString() : "N/A");

        String imageUrl = tableModel.getValueAt(selectedRow, 9) != null ? tableModel.getValueAt(selectedRow, 9).toString() : "";
        ImageIcon icon = ImageUtils.loadImageIcon(imageUrl, 100, 150);
        if (icon != null) {
            imagePreviewLabel.setIcon(icon);
            imagePreviewLabel.setText("");
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Image / Error");
        }
    }

    private void clearBookDetails() {
        JLabel displayTitle = (JLabel) searchResultsTable.getClientProperty("displayTitle");
        JLabel displayAuthor = (JLabel) searchResultsTable.getClientProperty("displayAuthor");
        JLabel displayCategory = (JLabel) searchResultsTable.getClientProperty("displayCategory");
        JLabel displayPrice = (JLabel) searchResultsTable.getClientProperty("displayPrice");
        JLabel displayQuantity = (JLabel) searchResultsTable.getClientProperty("displayQuantity");
        JLabel displayIsbn = (JLabel) searchResultsTable.getClientProperty("displayIsbn");
        JLabel displayPubDate = (JLabel) searchResultsTable.getClientProperty("displayPubDate");
        JTextArea displayDescription = (JTextArea) searchResultsTable.getClientProperty("displayDescription");
        JLabel imagePreviewLabel = (JLabel) searchResultsTable.getClientProperty("imagePreviewLabel");

        displayTitle.setText("");
        displayAuthor.setText("");
        displayCategory.setText("");
        displayPrice.setText("");
        displayQuantity.setText("");
        displayIsbn.setText("");
        displayPubDate.setText("");
        displayDescription.setText("");
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("Image Preview");
    }
}
