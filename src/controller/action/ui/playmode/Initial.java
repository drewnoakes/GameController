package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import data.GameState;
import data.PlayMode;
import rules.Rules;

/**
 * Sets play mode to {@link PlayMode#Initial}.
 *
 * @author Michel Bartsch
 */
public class Initial extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.playMode != PlayMode.Initial) {
            forceExecute(state);
            game.pushState("Initial");
        }
    }

    /**
     * Performs this action, even if the current play mode is {@link PlayMode#Initial}.
     */
    public void forceExecute(@NotNull GameState state)
    {
        if (Rules.league.returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Initial;
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode == PlayMode.Initial || state.testmode;
    }
}