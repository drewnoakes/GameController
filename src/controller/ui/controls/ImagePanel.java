package controller.ui.controls;

import common.annotations.Nullable;

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

    public enum Mode
    {
        Stretch,
        TopCentre
    }

    private final Mode mode;
    /** The image that is shown in the background. */
    @Nullable
    private Image image;

    /**
     * Creates a new ImagePanel.
     *
     * @param image     The Image to be shown in the background.
     */
    public ImagePanel(Mode mode, @Nullable Image image)
    {
        this.mode = mode;
        this.image = image;
    }

    public ImagePanel(Mode mode)
    {
        this.mode = mode;
    }

    /**
     * Changes the background image.
     *
     * @param image the new background image to use
     */
    public void setImage(@Nullable Image image)
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

        if (image == null)
            return;

        if (mode == Mode.Stretch) {
            g.drawImage(
                    image,
                    0, 0,
                    getWidth(), getHeight(),
                    null);
        } else if (mode == Mode.TopCentre) {
            g.drawImage(image,
                    (getWidth() - image.getWidth(null)) / 2, 0,
                    image.getWidth(null), image.getHeight(null),
                    null);
        }
    }
}
