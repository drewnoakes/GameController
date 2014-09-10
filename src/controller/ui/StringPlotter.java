package controller.ui;

import common.annotations.NotNull;

import java.awt.*;

/**
 * Writes lines of text to a {@link Graphics} object.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class StringPlotter
{
    private final Graphics g;
    private final int lineSpacing;
    private int x;
    private int y;

    public StringPlotter(@NotNull Graphics g, @NotNull Font font, @NotNull Color color, double lineSpacing)
    {
        this.g = g;
        this.lineSpacing = (int)Math.round(lineSpacing * font.getSize());

        g.setColor(color);
        g.setFont(font);
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void write(@NotNull String string)
    {
        g.drawString(string, x, y);
        y += lineSpacing;
    }
}
