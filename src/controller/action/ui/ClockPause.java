package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import controller.action.ActionBoard;

/**
 * This action means that the clock is to be paused.
 *
 * @author Michel Bartsch
 */
public class ClockPause extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (ActionBoard.clock.isClockRunning(game, state)) {
            if (state.isManPlay()) {
                state.setManRemainingGameTimeOffset(state.getManRemainingGameTimeOffset() + state.getTime() - state.getManWhenClockChanged());
                state.setManPlay(false);
            } else {
                state.setManWhenClockChanged(state.getTime());
                state.setManPause(true);
            }
            game.pushState("Time manual paused");
        } else {
            if (state.isManPause()) {
                state.setManPause(false);
                state.setManTimeOffset(state.getManTimeOffset() - (state.getTime() - state.getManWhenClockChanged()));
            } else {
                state.setManWhenClockChanged(state.getTime());
                state.setManPlay(true);
            }
            game.pushState("Time manual running");
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.isTestMode();
    }
}