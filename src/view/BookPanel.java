package view;

import controller.BookController;
import controller.CategoryController;
import model.Book;
import model.Category;
import utils.ImageUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector; // Import Vector for DefaultTableModel

/**
 * Panel for managing books (add, edit, delete, view).
 * This panel is typically accessible only by Owner users.
 */
public class BookPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookController bookController;
    private CategoryController categoryController;

    // Form components
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<Category> categoryComboBox;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField isbnField;
    private JTextField publicationDateField; // YYYY-MM-DD
    private JTextArea descriptionArea;
    private JTextField imageUrlField;
    private JLabel imagePreviewLabel;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    public BookPanel() {
        bookController = new BookController();
        categoryController = new CategoryController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        setupTable();
        addListeners();
        populateCategoryComboBox();
        refreshBookTable();
        // setFormEditable(false); // This is now handled by clearForm() called in refreshBookTable()
    }

    private void initComponents() {
        // Table Panel
        String[] columnNames = {"ID", "Title", "Author", "Category", "Price", "Quantity", "ISBN", "Pub. Date", "Description", "Image URL"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Book Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID (non-editable, for display)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        idField = new JTextField(15);
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Title
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 3; gbc.gridy = row++;
        titleField = new JTextField(25);
        formPanel.add(titleField, gbc);

        // Author
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        authorField = new JTextField(15);
        formPanel.add(authorField, gbc);

        // Category
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3; gbc.gridy = row++;
        categoryComboBox = new JComboBox<>();
        formPanel.add(categoryComboBox, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        priceField = new JTextField(15);
        formPanel.add(priceField, gbc);

        // Quantity
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3; gbc.gridy = row++;
        quantityField = new JTextField(15);
        formPanel.add(quantityField, gbc);

        // ISBN
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        isbnField = new JTextField(15);
        formPanel.add(isbnField, gbc);

        // Publication Date
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Pub. Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3; gbc.gridy = row++;
        publicationDateField = new JTextField(15);
        formPanel.add(publicationDateField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        gbc.gridwidth = 3; // Span across 3 columns
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        gbc.gridwidth = 1; // Reset gridwidth
        row++;

        // Image URL
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Image URL:"), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        gbc.gridwidth = 2;
        imageUrlField = new JTextField(25);
        formPanel.add(imageUrlField, gbc);
        gbc.gridwidth = 1;

        // Image Preview
        gbc.gridx = 3; gbc.gridy = row++;
        gbc.gridheight = 2; // Span two rows for image
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(100, 150)); // Standard book cover size
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        formPanel.add(imagePreviewLabel, gbc);
        gbc.gridheight = 1; // Reset gridheight

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        addButton = new JButton("Add New");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear Form");

        // Style buttons
        styleButton(addButton, new Color(46, 139, 87)); // SeaGreen
        styleButton(updateButton, new Color(30, 144, 255)); // DodgerBlue
        styleButton(deleteButton, new Color(220, 20, 60)); // Crimson
        styleButton(clearButton, new Color(105, 105, 105)); // DimGray

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
        button.setPreferredSize(new Dimension(100, 30));
    }

    private void setupTable() {
        // Set up column widths (optional, but good for presentation)
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Category
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(50);  // Price
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(50);  // Quantity
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Pub. Date
        bookTable.getColumnModel().getColumn(8).setPreferredWidth(200); // Description
        bookTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Image URL (can be hidden if not needed)
    }

    private void addListeners() {
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    displayBookDetails(selectedRow);
                    setFormEditable(true); // Enable form for editing
                    addButton.setEnabled(false); // Disable add when editing
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });

        addButton.addActionListener(e -> addNewBook());
        updateButton.addActionListener(e -> updateExistingBook());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        clearButton.addActionListener(e -> clearForm());

        imageUrlField.addActionListener(e -> loadImagePreview());
        imageUrlField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                loadImagePreview();
            }
        });
    }

    private void populateCategoryComboBox() {
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        List<Category> categories = categoryController.getAllCategories();
        for (Category category : categories) {
            model.addElement(category);
        }
        categoryComboBox.setModel(model);
    }

    /**
     * Refreshes the book table with the latest data from the database
     * and resets the form to a state ready for adding new books.
     */
    public void refreshBookTable() {
        bookController.populateBookTable(tableModel);
        clearForm(); // Clear form after refresh
        setFormEditable(false); // Disable form fields
        addButton.setEnabled(true); // Enable add button
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    /**
     * Displays book details from a selected row in the table into the form fields.
     * @param selectedRow The index of the selected row.
     */
    private void displayBookDetails(int selectedRow) {
        idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        titleField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        authorField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        String categoryName = tableModel.getValueAt(selectedRow, 3).toString();
        // Set selected category in combo box
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            if (categoryComboBox.getItemAt(i).getName().equals(categoryName)) {
                categoryComboBox.setSelectedIndex(i);
                break;
            }
        }
        priceField.setText(tableModel.getValueAt(selectedRow, 4).toString());
        quantityField.setText(tableModel.getValueAt(selectedRow, 5).toString());
        isbnField.setText(tableModel.getValueAt(selectedRow, 6) != null ? tableModel.getValueAt(selectedRow, 6).toString() : "");
        publicationDateField.setText(tableModel.getValueAt(selectedRow, 7) != null ? tableModel.getValueAt(selectedRow, 7).toString() : "");
        descriptionArea.setText(tableModel.getValueAt(selectedRow, 8) != null ? tableModel.getValueAt(selectedRow, 8).toString() : "");
        imageUrlField.setText(tableModel.getValueAt(selectedRow, 9) != null ? tableModel.getValueAt(selectedRow, 9).toString() : "");

        loadImagePreview();
    }

    /**
     * Public method to display book details in the form, typically called from other panels
     * (e.g., from HomePanel when an Owner clicks "Edit" on a book card).
     * @param book The Book object whose details are to be displayed.
     */
    public void displayBookDetails(Book book) {
        if (book == null) {
            clearForm();
            return;
        }
        idField.setText(String.valueOf(book.getId()));
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        // Set selected category in combo box
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            Category category = categoryComboBox.getItemAt(i);
            if (category != null && category.getId() == book.getCategoryId()) {
                categoryComboBox.setSelectedItem(category);
                break;
            }
        }
        priceField.setText(book.getPrice().toPlainString());
        quantityField.setText(String.valueOf(book.getQuantity()));
        isbnField.setText(book.getIsbn());
        publicationDateField.setText(book.getPublicationDate() != null ? book.getPublicationDate().toString() : "");
        descriptionArea.setText(book.getDescription());
        imageUrlField.setText(book.getImageUrl());

        loadImagePreview();
        setFormEditable(true); // Enable form for editing
        addButton.setEnabled(false); // Disable add when editing
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }


    private void loadImagePreview() {
        String imageUrl = imageUrlField.getText();
        ImageIcon icon = ImageUtils.loadImageIcon(imageUrl, 100, 150); // Standard size for book covers
        if (icon != null) {
            imagePreviewLabel.setIcon(icon);
            imagePreviewLabel.setText(""); // Clear text if image loads
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No Image / Error");
        }
    }

    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        categoryComboBox.setSelectedIndex(-1); // No selection
        priceField.setText("");
        quantityField.setText("");
        isbnField.setText("");
        publicationDateField.setText("");
        descriptionArea.setText("");
        imageUrlField.setText("");
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("Image Preview");

        bookTable.clearSelection();
        setFormEditable(true); // Enable fields for new entry
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void setFormEditable(boolean editable) {
        titleField.setEditable(editable);
        authorField.setEditable(editable);
        categoryComboBox.setEnabled(editable);
        priceField.setEditable(editable);
        quantityField.setEditable(editable);
        isbnField.setEditable(editable);
        publicationDateField.setEditable(editable);
        descriptionArea.setEditable(editable);
        imageUrlField.setEditable(editable);
    }

    private void addNewBook() {
        if (!validateForm()) return;

        String title = titleField.getText();
        String author = authorField.getText();
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        String categoryName = (selectedCategory != null) ? selectedCategory.getName() : null;
        BigDecimal price = new BigDecimal(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        String isbn = isbnField.getText();
        Date pubDate = parseDate(publicationDateField.getText());
        String description = descriptionArea.getText();
        String imageUrl = imageUrlField.getText();

        if (bookController.addBook(title, author, categoryName, price, quantity, isbn, pubDate, description, imageUrl)) {
            JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshBookTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add book. Check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExistingBook() {
        if (!validateForm() || idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a book to update or fill all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idField.getText());
        String title = titleField.getText();
        String author = authorField.getText();
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        String categoryName = (selectedCategory != null) ? selectedCategory.getName() : null;
        BigDecimal price = new BigDecimal(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        String isbn = isbnField.getText();
        Date pubDate = parseDate(publicationDateField.getText());
        String description = descriptionArea.getText();
        String imageUrl = imageUrlField.getText();

        if (bookController.updateBook(id, title, author, categoryName, price, quantity, isbn, pubDate, description, imageUrl)) {
            JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshBookTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update book. Check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a book selected in the table.
     */
    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        performDeleteBook(bookId);
    }

    /**
     * Public method to perform book deletion, typically called from other panels
     * (e.g., from HomePanel when an Owner clicks "Delete" on a book card).
     * @param bookId The ID of the book to delete.
     */
    public void performDeleteBook(int bookId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookController.deleteBook(bookId)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book. It might be referenced by purchases.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (titleField.getText().isEmpty() || authorField.getText().isEmpty() ||
                categoryComboBox.getSelectedItem() == null || priceField.getText().isEmpty() ||
                quantityField.getText().isEmpty() || publicationDateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Title, Author, Category, Price, Quantity, Pub. Date).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(priceField.getText());
            Integer.parseInt(quantityField.getText());
            parseDate(publicationDateField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Publication Date must be in YYYY-MM-DD format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false); // Strict parsing
            java.util.Date utilDate = formatter.parse(dateString);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.", e);
        }
    }
}
