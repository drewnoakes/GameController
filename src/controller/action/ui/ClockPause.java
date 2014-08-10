package controller.action.ui;

import common.annotations.NotNull;
import common.annotations.Nullable;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
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
        super(ActionTrigger.User);
    }

    @Override
    public void perform(@NotNull GameState state, @Nullable String message)
    {
        if (ActionBoard.clock.isClockRunning(state)) {
            if (state.manPlay) {
                state.manRemainingGameTimeOffset += state.getTime() - state.manWhenClockChanged;
                state.manPlay = false;
            } else {
                state.manWhenClockChanged = state.getTime();
                state.manPause = true;
            }
            log(state, message, "Time manual paused");
        } else {
            if (state.manPause) {
                state.manPause = false;
                state.manTimeOffset -= state.getTime() - state.manWhenClockChanged;
            } else {
                state.manWhenClockChanged = state.getTime();
                state.manPlay = true;
            }
            log(state, message, "Time manual running");
        }
    }

    @Override
    public boolean isLegal(GameState state)
    {
        return state.testmode;
    }
}