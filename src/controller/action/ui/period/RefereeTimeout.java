package controller.action.ui.period;

import common.annotations.NotNull;
import controller.Action;
import controller.Game;
import controller.action.ActionBoard;
import controller.action.ActionTrigger;
import controller.GameState;
import data.PlayMode;
import data.Period;

public class RefereeTimeout extends Action
{
    @Override
    public void execute(@NotNull Game game, @NotNull GameState state)
    {
        if (!state.refereeTimeout) {
            state.previousPeriod = state.period;
            state.period = Period.Timeout;
            state.refereeTimeout = true;
            if (state.playMode == PlayMode.Playing) {
                state.addTimeInCurrentPlayMode();
            }
            if (state.previousPeriod == Period.PenaltyShootout
                    && (state.playMode == PlayMode.Set || state.playMode == PlayMode.Playing)) {
                state.teams[state.nextKickOffColor == state.teams[0].teamColor ? 0 : 1].penaltyShot--;
            }
            game.apply(ActionBoard.initial, ActionTrigger.User);
            game.pushState("Referee Timeout");
        } else {
            state.period = state.previousPeriod;
            state.previousPeriod = Period.Timeout;
            state.refereeTimeout = false;
            if (state.period != Period.PenaltyShootout) {
                game.apply(ActionBoard.ready, ActionTrigger.User);
                game.pushState("End of Referee Timeout");
            }
        }
    }

    @Override
    public boolean canExecute(@NotNull Game game, @NotNull GameState state)
    {
        return state.playMode != PlayMode.Finished
                && !state.timeOutActive[0]
                && !state.timeOutActive[1];
    }
}
