package controller.action.ui;

import common.Log;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;

/**
 * This action means that the clock is to be resetted.
 *
 * @author Michel Bartsch
 */
public class ClockReset extends GCAction
{
    /**
     * Creates a new ClockReset action.
     * Look at the ActionBoard before using this.
     */
    public ClockReset()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     *
     * @param state      The current data to work on.
     */
    @Override
    public void perform(GameState state)
    {
        state.timeBeforeCurrentPlayMode = 0;
        state.whenCurrentPlayModeBegan = state.getTime();
        state.manWhenClockChanged = state.whenCurrentPlayModeBegan;
        state.manRemainingGameTimeOffset = 0;
        Log.state(state, "Time reset");
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     *
     * @param state      The current data to check with.
     */
    @Override
    public boolean isLegal(GameState state)
    {
        return state.testmode;
    }
}