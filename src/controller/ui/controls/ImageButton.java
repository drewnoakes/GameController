package controller.ui.controls;

import javax.swing.*;
import java.awt.*;

/**
 * This is a normal JButton, but it has a background image.
 *
 * @author Michel Bartsch
 */
public class ImageButton extends JButton
{
    private static final long serialVersionUID = 1L;


    /** The image that is shown in the background. */
    private Image image;

    /**
     * Creates a new ImageButton.
     *
     * @param image     The Image to be shown in the background.
     */
    public ImageButton(Image image)
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
            g.clearRect(0, 0, getWidth(), getHeight());
        }
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
}
