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
    public ClockPause()
    {
        super(ActionType.UI);
    }

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
    
    @Override
    public boolean isLegal(GameState state)
    {
        return state.testmode;
    }
}