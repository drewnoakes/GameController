package visualizer;

import common.annotations.NotNull;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 * Binds keyboard events to visualiser actions.
 *
 * @author Michel Bartsch
 * @author Drew Noakes https://drewnoakes.com
 */
public class KeyboardListener implements KeyEventDispatcher
{
    /**
     * The instance of the visualizer´s gui.
     */
    private final VisualizerUI ui;
    /**
     * The key that is currently depressed, or 0.
     */
    private int pressing = 0;

    /**
     * Initialise a new KeyboardListener.
     */
    public KeyboardListener(@NotNull VisualizerUI ui)
    {
        this.ui = ui;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    /**
     * Process key events, such as key presses and releases.
     *
     * @param e The key that has been pressed or released.
     * @return <code>true</code> if the key was processed, otherwise <code>false</code>
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            pressing = 0;
        } else if (e.getID() == KeyEvent.KEY_PRESSED) {
            int key = e.getKeyCode();

            if (key == 0 || key == pressing) {
                return false;
            }

            // This is a new key press -- process it
            pressing = key;
            return onKeyPress(e);
        }

        return false;
    }

    /**
     * Handle a key press event.
     *
     * @param e the key event
     * @return <code>true</code> if the key was processed, otherwise <code>false</code>
     */
    private boolean onKeyPress(KeyEvent e)
    {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F10:
                Main.exit();
                break;
            case KeyEvent.VK_F11:
                ui.toggleTestmode();
                break;
            case KeyEvent.VK_Q:
                if (e.isControlDown())
                    Main.exit();
                break;
            case KeyEvent.VK_M:
            case KeyEvent.VK_F:
                ui.mirrorTeams();
                break;
            default:
                return false;
        }
        return true;
    }
}