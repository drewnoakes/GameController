package controller.action.ui.playmode;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.GameState;
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
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (state.playMode == PlayMode.Ready) {
            return;
        }
        forceExecute(game, state);
        game.pushState("Ready");
    }

    public void forceExecute(Game game, GameState state)
    {
        if (game.settings().returnRobotsInGameStoppages) {
            state.resetPenaltyTimes();
        }
        if (state.playMode == PlayMode.Playing) {
            state.addTimeInCurrentPlayMode();
        }
        state.whenCurrentPlayModeBegan = state.getTime();
        state.playMode = PlayMode.Ready;
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return
            (state.playMode == PlayMode.Initial
              && !state.timeOutActive[0]
              && !state.timeOutActive[1]
              && !state.refereeTimeout
              && state.period != Period.PenaltyShootout)
            || state.playMode == PlayMode.Ready
            || state.testmode;
    }
}
