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
        if (state.is(PlayMode.Ready)) {
            return;
        }
        forceExecute(game, state);
        game.pushState("Ready");
    }

    public void forceExecute(Game game, WriteableGameState state)
    {
        if (game.rules().arePenaltiesClearedDuringStoppages()) {
            state.setRemainingPenaltyTimesToZero();
        }
        if (state.is(PlayMode.Playing)) {
            state.addTimeInCurrentPlayMode();
        }
        state.setWhenCurrentPlayModeBegan(state.getTime());
        state.setPlayMode(PlayMode.Ready);
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull ReadOnlyGameState state)
    {
        return
            (state.is(PlayMode.Initial)
              && !state.isTimeoutActive()
              && !state.isRefereeTimeoutActive()
              && !state.is(Period.PenaltyShootout))
            || state.is(PlayMode.Ready)
            || state.isTestMode();
    }
}
