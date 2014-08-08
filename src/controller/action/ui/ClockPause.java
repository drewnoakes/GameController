package controller.action.ui;

import common.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.GameState;

/**
 * This action means that the clock is to be paused.
 *
 * @author Michel Bartsch
 */
public class ClockPause extends GCAction
{
    /**
     * Creates a new ClockPause action.
     * Look at the ActionBoard before using this.
     */
    public ClockPause()
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
        if (ActionBoard.clock.isClockRunning(state)) {
            if (state.manPlay) {
                state.manRemainingGameTimeOffset += state.getTime() - state.manWhenClockChanged;
                state.manPlay = false;
            } else {
                state.manWhenClockChanged = state.getTime();
                state.manPause = true;
            }
            Log.state(state, "Time manual paused");
        } else {
            if (state.manPause) {
                state.manPause = false;
                state.manTimeOffset -= state.getTime() - state.manWhenClockChanged;
            } else {
                state.manWhenClockChanged = state.getTime();
                state.manPlay = true;
            }
            Log.state(state, "Time manual running");
        }
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