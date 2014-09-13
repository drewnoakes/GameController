package controller.action.ui;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;

public class IncGameClock extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        state.setTimeBeforeCurrentPlayMode(state.getTimeBeforeCurrentPlayMode() - 1000*60);
        game.pushState("Increase Game Clock");
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return (!state.is(PlayMode.Playing) && state.getTimeBeforeCurrentPlayMode() >= 1000*60)
            || state.isTestMode();
    }
}
