package controller.ui.controls;

import javax.swing.*;
import java.awt.*;

/** Fix button centering for Apple Java. */
public class ToggleButton extends JToggleButton
{
    private static final boolean IS_OSX = System.getProperty("os.name").contains("OS X");
    private static final boolean IS_APPLE_JAVA = IS_OSX && System.getProperty("java.version").compareTo("1.7") < 0;
    private static final Insets insets = IS_APPLE_JAVA ? new Insets (2, -30, 2, -30) : null;
    private static final String BUTTON_MASK = IS_APPLE_JAVA
            ? "<html><div style=\"padding: 0px 12px\"><center>%s</center></div></html>"
            : "<html><center>%s</center></html>";

    public ToggleButton()
    {
        setMargin(insets);
    }

    public ToggleButton(String text)
    {
        setMargin(insets);
        setText(text);
    }

    public void setText(String text)
    {
        super.setText(String.format(BUTTON_MASK, text));
    }
}
