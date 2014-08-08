package controller.action;

import controller.EventHandler;
import data.GameState;
import data.PlayerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is an abstract class every action needs to extend to provide the
 * basic features. By extending this it also becomes an ActionListener,
 * which is needed to add them to GUI buttons.
 *
 * @author Michel Bartsch
 */
public abstract class GCAction implements ActionListener
{
    /** The type of the action. */
    public final ActionType type;

    /**
     * Initialises a @{link GCAction}.
     * 
     * @param type      The type of the action.
     */
    public GCAction(ActionType type)
    {
        this.type = type;
    }
    
    /**
     * This gets called when the button an action is added to was pushed or
     * if the action is called otherwise.
     * 
     * The action`s perform method will not be executed right away but
     * later in the GUI`s thread.
     * 
     * @param e the event that happened, but this is ignored.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        EventHandler.getInstance().register(this);
    }
    
    /**
     * This is the essential method of each action.
     * It is called automatically after the actionPerformed method was called.
     * Here you can manipulate the given data without worrying.
     *
     * @param state the game state to operate on.
     */
    public abstract void perform(GameState state);
    
    /**
     * Perform a action on a specific player.
     *
     * Subclasses only override this function if needed.
     *
     * @param state the game state to operate on.
     * @param player the player on which the action is to be performed.
     * @param side the side this player is playing for, 0: left, 1: right
     * @param number the players number, beginning at 0!
     */
    public void performOn(GameState state, PlayerInfo player, int side, int number)
    {}
    
    /**
     * Must be override to determine if the action is legal at a specific
     * state of the game.
     *
     * Actions that are not legal will not be executed by the EventHandler.
     * 
     * @param state the current data to calculate the legality by.
     * @return This is true if the action is legal.
     */
    public abstract boolean isLegal(GameState state);
}