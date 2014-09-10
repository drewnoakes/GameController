package controller.ui.controls;

import common.annotations.NotNull;
import common.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * A subclass of {@link JPanel}, which allows setting an image in the background,
 * and a custom background colour.
 * <p>
 * The color is only used when {@link #isOpaque()} returns <code>true</code>,
 * the {@link Mode} is not {@link Mode#Stretch}, or no {@link Image} is specified.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class ImagePanel extends JPanel
{
    public enum Mode
    {
        Stretch,
        TopCentre
    }

    @NotNull
    private final Mode mode;
    /** The image that is shown in the background. */
    @Nullable
    private Image image;

    /**
     * Initialises an {@link ImagePanel}.
     *
     * @param image  the {@link Image} to be shown in the background.
     */
    public ImagePanel(@NotNull Mode mode, @Nullable Image image)
    {
        this.mode = mode;
        this.image = image;
    }

    public ImagePanel(@NotNull Mode mode)
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
     * Paints custom presentation for the surface of this component.
     *
     * @param g the {@link Graphics} context to paint with.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        if (image == null) {
            paintBackgroundColour(g);
            return;
        }

        if (mode == Mode.Stretch) {
            g.drawImage(
                    image,
                    0, 0,
                    getWidth(), getHeight(),
                    null);
        } else if (mode == Mode.TopCentre) {
            paintBackgroundColour(g);
            g.drawImage(
                    image,
                    (getWidth() - image.getWidth(null)) / 2, 0,
                    image.getWidth(null), image.getHeight(null),
                    null);
        }
    }

    private void paintBackgroundColour(Graphics g)
    {
        if (super.isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
