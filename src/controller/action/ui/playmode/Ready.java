package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.*;
import data.Period;
import data.PlayMode;

/**
 * Sets play mode to {@link PlayMode#Ready}.
 *
 * @author Michel Bartsch
 */
public class Ready extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull WriteableGameState state)
    {
        if (state.getPlayMode() == PlayMode.Ready) {
            return;
        }
        forceExecute(game, state);
        game.pushState("Ready");
    }

    public void forceExecute(Game game, WriteableGameState state)
    {
        if (game.settings().returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        if (state.getPlayMode() == PlayMode.Playing) {
            state.addTimeInCurrentPlayMode();
        }
        state.setWhenCurrentPlayModeBegan(state.getTime());
        state.setPlayMode(PlayMode.Ready);
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return
            (state.getPlayMode() == PlayMode.Initial
              && !state.isTimeOutActive()
              && !state.isRefereeTimeoutActive()
              && state.getPeriod() != Period.PenaltyShootout)
            || state.getPlayMode() == PlayMode.Ready
            || state.isTestMode();
    }
}
