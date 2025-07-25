package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Panel for managing users (add, edit, delete, view).
 * This panel is typically accessible only by Owner users.
 */
public class UserPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    // Form components
    private JTextField idField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    public UserPanel() {
        userDAO = new UserDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        setupTable();
        addListeners();
        refreshUserTable();
        setFormEditable(false); // Initially disable form fields
    }

    private void initComponents() {
        // Table Panel
        String[] columnNames = {"ID", "Username", "Role"}; // Password is not displayed
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(500, 250));
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "User Details"));
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

        // Username
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        roleComboBox = new JComboBox<>(new String[]{"Owner", "User"}); // Changed "Admin" to "Owner"
        formPanel.add(roleComboBox, gbc);

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
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Role
    }

    private void addListeners() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    displayUserDetails(selectedRow);
                    setFormEditable(true); // Enable form for editing
                    addButton.setEnabled(false); // Disable add when editing
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });

        addButton.addActionListener(e -> addNewUser());
        updateButton.addActionListener(e -> updateExistingUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        clearButton.addActionListener(e -> clearForm());
    }

    private void refreshUserTable() {
        tableModel.setRowCount(0); // Clear existing data
        java.util.List<User> users = userDAO.getAllUsers();

        for (User user : users) {
            Vector<Object> row = new Vector<>();
            row.add(user.getId());
            row.add(user.getUsername());
            row.add(user.getRole());
            tableModel.addRow(row);
        }
        clearForm(); // Clear form after refresh
        setFormEditable(false); // Disable form fields
        addButton.setEnabled(true); // Enable add button
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void displayUserDetails(int selectedRow) {
        idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
        // Password is not retrieved for security reasons, user must re-enter for update
        passwordField.setText("");
        roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
    }

    private void clearForm() {
        idField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0); // Default to first role
        userTable.clearSelection();
        setFormEditable(true); // Enable fields for new entry
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void setFormEditable(boolean editable) {
        usernameField.setEditable(editable);
        passwordField.setEditable(editable);
        roleComboBox.setEnabled(editable);
    }

    private void addNewUser() {
        if (!validateForm()) return;

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (userDAO.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different one.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(username, password, role);
        if (userDAO.addUser(user)) {
            JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshUserTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user. Check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExistingUser() {
        if (!validateForm() || idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user to update or fill all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idField.getText());
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword()); // Get new password
        String role = (String) roleComboBox.getSelectedItem();

        // If password field is empty, keep existing password (not ideal, but for simplicity)
        // In a real app, you'd fetch the old hash or force password change.
        User existingUser = userDAO.getUserById(id);
        if (existingUser == null) {
            JOptionPane.showMessageDialog(this, "User not found for update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.isEmpty()) {
            password = existingUser.getPassword(); // Use existing password if not changed
        }

        User user = new User(id, username, password, role);
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshUserTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user. Check logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String usernameToDelete = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user: " + usernameToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user. They might be referenced by purchases.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (usernameField.getText().isEmpty() || new String(passwordField.getPassword()).isEmpty() || roleComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Username, Password, Role).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
