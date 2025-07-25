package view;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application frame for the Book Stock Management System.
 * Contains a tabbed pane for different functional panels.
 */
public class MainFrame extends JFrame {
    private User loggedInUser;
    private JTabbedPane tabbedPane;

    public MainFrame(User user) {
        this.loggedInUser = user;
        setTitle("Book Stock Management System - Logged in as: " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle closing manually
        setLocationRelativeTo(null); // Center the window

        initComponents();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure you want to exit?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (confirm == 0) {
                    System.exit(0);
                }
            }
        });
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Add panels based on user role
        tabbedPane.addTab("Home", new HomePanel(loggedInUser)); // New Home Panel for User features
        tabbedPane.addTab("Dashboard", new DashboardPanel(loggedInUser));
        tabbedPane.addTab("Search Books", new SearchPanel());

        if ("Owner".equals(loggedInUser.getRole())) { // Changed from "Admin" to "Owner"
            tabbedPane.addTab("Books", new BookPanel());
            tabbedPane.addTab("Categories", new CategoryPanel());
            tabbedPane.addTab("Users", new UserPanel());
            // Potentially add Purchase management for Owner
            // tabbedPane.addTab("Purchases", new PurchasePanel());
        }

        add(tabbedPane, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 20, 60)); // Crimson
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showOptionDialog(
                    this, "Are you sure you want to logout?", "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
            if (confirm == 0) {
                new LoginFrame().setVisible(true);
                this.dispose(); // Close MainFrame
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel("  Logged in as: " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")  ");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(userLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }
}


