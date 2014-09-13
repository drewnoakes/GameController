package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.*;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Initial}.
 *
 * @author Michel Bartsch
 */
public class Initial extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (!state.is(PlayMode.Initial)) {
            forceExecute(game, state);
            game.pushState("Initial");
        }
    }

    /**
     * Performs this action, even if the current play mode is {@link PlayMode#Initial}.
     */
    public void forceExecute(Game game, @NotNull WriteableGameState state)
    {
        if (game.settings().returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        state.setWhenCurrentPlayModeBegan(state.getTime());
        state.setPlayMode(PlayMode.Initial);
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return state.is(PlayMode.Initial) || state.isTestMode();
    }
}