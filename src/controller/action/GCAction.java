package controller.action;

import common.Log;
import common.annotations.NotNull;
import common.annotations.Nullable;
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
     * Initialises a {@link GCAction}.
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
     * Executes the action on the specified {@link GameState}.
     *
     * @param state the game state to operate on.
     * @param message the message to associate with this action. If null, the GCAction applies its default message.
     */
    public abstract void perform(@NotNull GameState state, @Nullable String message);

    /**
     * Executes the action on the specified {@link GameState}.
     *
     * @param state the game state to operate on.
     */
    public void perform(@NotNull GameState state)
    {
        perform(state, null);
    }

    /**
     * Executes the action against a specific player on the specified {@link GameState}.
     *
     * Subclasses only override this function if needed.
     *
     * @param state the game state to operate on.
     * @param player the player on which the action is to be performed.
     * @param side the side this player is playing for, 0: left, 1: right
     * @param number the players number, beginning at 0!
     */
    public void performOn(@NotNull GameState state, @NotNull PlayerInfo player, int side, int number)
    {}

    /**
     * Records the game state after the execution of the action.
     *
     * @param state the state after the action completes
     * @param overrideMessage a custom message as provided by the caller of {@link GCAction#perform}.
     * @param defaultMessage the default message, used if <code>overrideMessage</code> is null.
     */
    protected void log(GameState state, String overrideMessage, String defaultMessage)
    {
        Log.state(state, overrideMessage == null ? defaultMessage : overrideMessage);
    }

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