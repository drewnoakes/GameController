package controller.ui;

import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import data.TeamColor;
import leagues.HL;
import leagues.SPL;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 * This class listens to the keyboard. It does not depend on the GUI.
 *
 * @author Michel Bartsch
 */
public class KeyboardListener implements KeyEventDispatcher
{
    private final Game game;
    /** The key that is actually pressed, 0 if no key is pressed. */
    private int pressing = 0;
    
    /**
     * Creates a new KeyboardListener and sets himself to listening.
     */
    public KeyboardListener(Game game)
    {
        this.game = game;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }
    
    /**
     * This is called every time a key is pressed or released.
     * 
     * @param e     The key that has been pressed or released.
     * 
     * @return If false, the key will be consumed.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            pressing = 0;
        } else if (e.getID() == KeyEvent.KEY_PRESSED) {
            int key = e.getKeyCode();
        
            if ((key == 0) || (key == pressing)) {
                return false;
            }
            pressing = key;
            return onKeyPress(key);
        }
        
        return false;
    }

    public void close()
    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    }
    
    /**
     * This is called once every time a key is pressed. It is called once and
     * not as long as the key is pressed.
     *
     * You can easily set the keys for each action here. The actions are
     * to be performed via the actionPerformed method as they are in the
     * GUI.
     * 
     * @param key the pressed key.
     * 
     * @return true if the keystroke was handled, otherwise false.
     */
    private boolean onKeyPress(int key)
    {
        Action action = null;
        
        switch (key) {
            case KeyEvent.VK_ESCAPE: action = ActionBoard.quit; break;
            case KeyEvent.VK_DELETE: action = ActionBoard.testmode; break;
            case KeyEvent.VK_BACK_SPACE: action = ActionBoard.undo[1]; break;

            case KeyEvent.VK_B: action = ActionBoard.out[game.getGameState().team[0].teamColor == TeamColor.Blue ? 0 : 1]; break;
            case KeyEvent.VK_R: action = ActionBoard.out[game.getGameState().team[0].teamColor == TeamColor.Red ? 0 : 1]; break;

            default:
                if (Game.settings instanceof SPL) {
                    switch (key) {
                        case KeyEvent.VK_P: action = ActionBoard.pushing; break;
                        case KeyEvent.VK_L: action = ActionBoard.leaving; break;
                        case KeyEvent.VK_F: action = ActionBoard.fallen; break;
                        case KeyEvent.VK_I: action = ActionBoard.inactive; break;
                        case KeyEvent.VK_D: action = ActionBoard.defender; break;
                        case KeyEvent.VK_O: action = ActionBoard.holding; break;
                        case KeyEvent.VK_H: action = ActionBoard.hands; break;
                        case KeyEvent.VK_U: action = ActionBoard.pickUpSPL; break;
                        case KeyEvent.VK_C: action = ActionBoard.coachMotion; break;
                        case KeyEvent.VK_T: action = ActionBoard.teammatePushing; break;
                        case KeyEvent.VK_S: action = ActionBoard.substitute; break;
                    }
                } else if (Game.settings instanceof HL) {
                    switch (key) {
                        case KeyEvent.VK_P: action = ActionBoard.pushing; break;
                        case KeyEvent.VK_D: action = ActionBoard.defense; break;
                        case KeyEvent.VK_M: action = ActionBoard.ballManipulation; break;
                        case KeyEvent.VK_I: action = ActionBoard.pickUpHL; break;
                        case KeyEvent.VK_A: action = ActionBoard.attack; break;
                        case KeyEvent.VK_S: action = ActionBoard.substitute; break;
                    }
                }
        }
        
        if (action != null) {
            game.apply(action, ActionTrigger.User);
            return true;
        }

        return false;
    }
}
