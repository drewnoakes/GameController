package controller.ui.controls;

import common.Event;

import javax.swing.*;
import java.awt.*;

/**
 * A simple extension of {@link JFrame} that fires an event before painting occurs.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class PaintableFrame extends JFrame
{
    public final Event<Graphics> beforePaint = new Event<Graphics>();

    public PaintableFrame(String title) throws HeadlessException
    {
        super(title);
    }

    @Override
    public void paint(Graphics g)
    {
        beforePaint.fire(g);

        super.paint(g);
    }
}
