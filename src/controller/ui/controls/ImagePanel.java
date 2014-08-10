package controller.ui.controls;

import javax.swing.*;
import java.awt.*;

/**
 * This is a normal JPanel, but it has a background image.
 *
 * @author Michel Bartsch
 */
public class ImagePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /** The image that is shown in the background. */
    private Image image;

    /**
     * Creates a new ImagePanel.
     *
     * @param image     The Image to be shown in the background.
     */
    public ImagePanel(Image image)
    {
        this.image = image;
    }

    /**
     * Changes the background image.
     *
     * @param image     Changes the image to this one.
     */
    public void setImage(Image image)
    {
        this.image = image;
    }

    /**
     * Paints this Component, should be called automatically.
     *
     * @param g     This components graphical content.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        if (super.isOpaque()) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
}
