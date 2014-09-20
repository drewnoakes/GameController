package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Finished}.
 *
 * @author Michel Bartsch
 */
public class Finish extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.is(PlayMode.Finished)) {
            return;
        }
        if (game.rules().isReturnRobotsInGameStoppages()) {
            state.resetPenaltyTimes();
        }
        state.addTimeInCurrentPlayMode();
        state.setWhenCurrentPlayModeBegan(state.getTime());
        state.setPlayMode(PlayMode.Finished);

        game.pushState("Finished");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Ready, PlayMode.Set, PlayMode.Playing, PlayMode.Finished)
            || state.isTestMode();
    }
}