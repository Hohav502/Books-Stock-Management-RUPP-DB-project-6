package view;


import controller.CategoryController;
import model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Panel for managing book categories (add, edit, delete, view).
 * This panel is typically accessible only by Owner users.
 */
public class CategoryPanel extends JPanel {
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private CategoryController categoryController;

    // Form components
    private JTextField idField;
    private JTextField nameField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    public CategoryPanel() {
        categoryController = new CategoryController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        setupTable();
        addListeners();
        refreshCategoryTable();
        setFormEditable(false); // Initially disable form fields
    }

    private void initComponents() {
        // Table Panel
        String[] columnNames = {"ID", "Category Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryTable.setRowHeight(25);
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Category Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID (non-editable, for display)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        idField = new JTextField(15);
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

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
        gbc.gridwidth = 2;
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
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Category Name
    }

    private void addListeners() {
        categoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = categoryTable.getSelectedRow();
                if (selectedRow != -1) {
                    displayCategoryDetails(selectedRow);
                    setFormEditable(true); // Enable form for editing
                    addButton.setEnabled(false); // Disable add when editing
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });

        addButton.addActionListener(e -> addNewCategory());
        updateButton.addActionListener(e -> updateExistingCategory());
        deleteButton.addActionListener(e -> deleteSelectedCategory());
        clearButton.addActionListener(e -> clearForm());
    }

    private void refreshCategoryTable() {
        categoryController.populateCategoryTable(tableModel);
        clearForm(); // Clear form after refresh
        setFormEditable(false); // Disable form fields
        addButton.setEnabled(true); // Enable add button
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void displayCategoryDetails(int selectedRow) {
        idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        categoryTable.clearSelection();
        setFormEditable(true); // Enable fields for new entry
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void setFormEditable(boolean editable) {
        nameField.setEditable(editable);
    }

    private void addNewCategory() {
        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = nameField.getText();
        if (categoryController.addCategory(name)) {
            JOptionPane.showMessageDialog(this, "Category added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCategoryTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add category. It might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExistingCategory() {
        if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a category to update or fill the name field.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        if (categoryController.updateCategory(id, name)) {
            JOptionPane.showMessageDialog(this, "Category updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCategoryTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update category. It might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category? Books associated with this category will have their category set to NULL.", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (categoryController.deleteCategory(categoryId)) {
                JOptionPane.showMessageDialog(this, "Category deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCategoryTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category. It might be referenced by books.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

