package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Playing}.
 *
 * @author Michel Bartsch
 */
public class Play extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.playMode == PlayMode.Playing) {
            return;
        }
        if (!game.isPlayOff() && state.timeBeforeCurrentPlayMode != 0) {
            state.addTimeInCurrentPlayMode();
        }
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Playing;
        game.pushState("Playing");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Set
            || state.playMode == PlayMode.Playing
            || state.testmode;
    }
}