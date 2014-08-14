package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Finished}.
 *
 * @author Michel Bartsch
 */
public class Finish extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.playMode == PlayMode.Finished) {
            return;
        }
        if (game.settings().returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        state.addTimeInCurrentPlayMode();
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Finished;

        game.pushState("Finished");
    }
    
    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Ready
            || state.playMode == PlayMode.Set
            || state.playMode == PlayMode.Playing
            || state.playMode == PlayMode.Finished
            || state.testmode;
    }
}