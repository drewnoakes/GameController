package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Playing}.
 *
 * @author Michel Bartsch
 */
public class Play extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.is(PlayMode.Playing)) {
            return;
        }
        if (!game.isPlayOff() && state.getTimeBeforeCurrentPlayMode() != 0) {
            state.addTimeInCurrentPlayMode();
        }
        state.setWhenCurrentPlayModeBegan(state.getTime());
        state.setPlayMode(PlayMode.Playing);
        game.pushState("Playing");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Set, PlayMode.Playing)
            || state.isTestMode();
    }
}