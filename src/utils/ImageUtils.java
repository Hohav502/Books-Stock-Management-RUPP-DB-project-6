package utils;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

/**
 * Utility class for image operations, primarily for loading and scaling images for GUI.
 */
public class ImageUtils {

    /**
     * Loads an image from a given URL and scales it to the specified width and height.
     * @param imageUrl The URL string of the image.
     * @param width The desired width.
     * @param height The desired height.
     * @return An ImageIcon scaled to the specified dimensions, or null if loading fails.
     */
    public static ImageIcon loadImageIcon(String imageUrl, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        try {
            URL url = new URL(imageUrl);
            ImageIcon originalIcon = new ImageIcon(url);
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading or scaling image from URL: " + imageUrl + " - " + e.getMessage());
            // Return a placeholder or null if image cannot be loaded
            return null;
        }
    }

    /**
     * Creates a placeholder image icon.
     * @param width Desired width.
     * @param height Desired height.
     * @return A placeholder ImageIcon.
     */
    public static ImageIcon createPlaceholderImageIcon(int width, int height) {
        // You can create a simple blank image or load a default "no image" icon here
        return new ImageIcon(new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB));
    }
}
