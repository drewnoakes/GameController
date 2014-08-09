package controller.action;

import controller.EventHandler;
import data.GameState;
import data.PlayerInfo;

/**
 * This is an abstract class every action needs to extend to provide the
 * basic features.
 *
 * @author Michel Bartsch
 */
public abstract class GCAction
{
    /** The type of the action. */
    public final ActionType type;

    /**
     * Initialises a @{link GCAction}.
     * 
     * @param type the type of the action.
     */
    protected GCAction(ActionType type)
    {
        this.type = type;
    }

    /** Causes this action to register itself for execution. */
    public void invoke()
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
     * Specifies if this action is legal at a specific state of the game.
     *
     * Actions that are not legal will not be executed by the EventHandler.
     *
     * By default, actions are legal unless this method is overridden.
     * 
     * @param state the current data to calculate the legality by.
     * @return This is true if the action is legal.
     */
    public boolean isLegal(GameState state)
    {
        return true;
    }
}