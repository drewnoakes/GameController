package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import data.GameState;

/**
 * This action means that the clock is to be paused.
 *
 * @author Michel Bartsch
 */
public class ClockPause extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (ActionBoard.clock.isClockRunning(state)) {
            if (state.manPlay) {
                state.manRemainingGameTimeOffset += state.getTime() - state.manWhenClockChanged;
                state.manPlay = false;
            } else {
                state.manWhenClockChanged = state.getTime();
                state.manPause = true;
            }
            game.pushState("Time manual paused");
        } else {
            if (state.manPause) {
                state.manPause = false;
                state.manTimeOffset -= state.getTime() - state.manWhenClockChanged;
            } else {
                state.manWhenClockChanged = state.getTime();
                state.manPlay = true;
            }
            game.pushState("Time manual running");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.testmode;
    }
}