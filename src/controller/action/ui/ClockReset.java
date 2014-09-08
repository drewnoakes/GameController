package controller.action.ui;

import common.annotations.NotNull;
import controller.*;

/**
 * This action means that the clock is to be reset.
 *
 * @author Michel Bartsch
 */
public class ClockReset extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        state.setTimeBeforeCurrentPlayMode(0);
        state.setWhenCurrentPlayModeBegan(state.getTime());
        state.setManWhenClockChanged(state.getWhenCurrentPlayModeBegan());
        state.setManRemainingGameTimeOffset(0);
        game.pushState("Time reset");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.isTestMode();
    }
}