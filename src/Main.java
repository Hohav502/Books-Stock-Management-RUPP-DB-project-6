import dao.DBConnection;
import view.LoginFrame;

import javax.swing.*;

/**
 * Main application class for the Book Stock Management System.
 * Initializes the database and starts the Login Frame.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize the database schema and insert initial data
        DBConnection.initializeDatabase();

        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Start the login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}