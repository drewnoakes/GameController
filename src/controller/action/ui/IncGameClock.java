package controller.action.ui;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.GameState;
import data.PlayMode;

public class IncGameClock extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        state.timeBeforeCurrentPlayMode -= 1000*60;
        game.pushState("Increase Game Clock");
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return (state.playMode != PlayMode.Playing && state.timeBeforeCurrentPlayMode >= 1000*60)
            || state.testmode;
    }
}
