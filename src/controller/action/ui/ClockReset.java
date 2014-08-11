package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.GameState;

/**
 * This action means that the clock is to be reset.
 *
 * @author Michel Bartsch
 */
public class ClockReset extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        state.timeBeforeCurrentPlayMode = 0;
        state.whenCurrentPlayModeBegan = state.getTime();
        state.manWhenClockChanged = state.whenCurrentPlayModeBegan;
        state.manRemainingGameTimeOffset = 0;
        game.pushState("Time reset");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.testmode;
    }
}